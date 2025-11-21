package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.LoginDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioUpdateDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.entities.PasswordResetToken;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.services.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.mapadavida.mdvBackend.security.JwtTokenProvider;
import com.mapadavida.mdvBackend.security.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private FileStorageService fileStorageService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final byte[] salt = "MDV".getBytes();

    @Value("${app.frontend.url:http://localhost:3000}")
    private String appFrontendUrl;

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getSenha()
                    )
            );


            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

            // Buscar o usuário completo para retornar no response
            UsuarioDTO usuarioDTO = usuarioService.getUsuarioById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("usuario", usuarioDTO);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Email ou senha inválidos"));
        }
    }



    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || ! (auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        // Carrega o DTO do usuário a partir do id presente no UserPrincipal
        Optional<UsuarioDTO> usuarioOpt = usuarioService.getUsuarioById(userPrincipal.getId());
        return usuarioOpt.map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    private String criptografar(String texto) {
        MessageDigest digest;
        byte[] passwordHash;
        StringBuilder sb = new StringBuilder();
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            passwordHash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < passwordHash.length; i++) {
                sb.append(Integer.toString((passwordHash[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("Erro ao calcular hash: {}", e.getMessage(), e);
        }
        return sb.toString();
    }

    @PostMapping(value = "/cadastrar")
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody Usuario usu) {
        Optional<Usuario> usuarioExistente = usuarioService.findByEmail(usu.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            if (usu.getEndereco() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Endereco enderecoNormalizado = enderecoService.findOrSave(usu.getEndereco());
            usu.setEndereco(enderecoNormalizado);

            // delegate password encoding to UsuarioService (BCrypt)
            Usuario usuarioSalvo = usuarioService.createUser(usu);
            return ResponseEntity.ok(new UsuarioDTO(usuarioSalvo));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.getAllUsuarios();
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarios);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tipoUsuario/{tipoUsuario}")
    public ResponseEntity<Map<String, Object>> getUsuariosByTipo(@PathVariable TipoUsuario tipoUsuario) {
        try {
            List<UsuarioDTO> usuarios = usuarioService.getUsuariosByTipo(tipoUsuario);
            if (usuarios.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("usuarios", usuarios);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUsuario(@PathVariable Long id, @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        try {
            UsuarioDTO updatedUsuario = usuarioService.updateUsuario(id, usuarioUpdateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", updatedUsuario);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuariosPorNome(@RequestParam String nome) {
        List<UsuarioDTO> usuarios = usuarioService.buscarUsuariosPorNome(nome);
        return ResponseEntity.ok(usuarios);
    }

    // POST /usuarios/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body != null ? body.get("email") : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "email_obrigatorio"));
        }

        // responde sempre com mensagem genérica
        ResponseEntity<?> generic = ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, você receberá instruções para redefinir a senha."));

        try {
            Optional<Usuario> userOpt = usuarioService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return generic;
            }
            Usuario user = userOpt.get();
            // gerar token e salvar hash
            PasswordResetService.TokenPair pair = passwordResetService.generateTokenForUser(user);
            String tokenPlain = pair.token;
            // montar link
            String resetUrl = appFrontendUrl + "/reset-password?token=" + java.net.URLEncoder.encode(tokenPlain, java.nio.charset.StandardCharsets.UTF_8);
            // enviar e-mail (não bloquear resposta)
            emailService.sendResetPasswordEmail(user.getEmail(), user.getNome(), resetUrl);
        } catch (Exception ex) {
            // log e siga em frente
            System.err.println("Erro em forgotPassword: " + ex.getMessage());
        }

        return generic;
    }

    // GET /usuarios/validate-reset-token?token=...
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        if (token == null || token.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "token_obrigatorio"));
        String hash = passwordResetService.hashToken(token);
        Optional<PasswordResetToken> rec = passwordResetService.findByTokenHash(hash);
        if (rec.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "token_invalido"));
        PasswordResetToken prt = rec.get();
        if (prt.isUsed() || prt.getExpiresAt().isBefore(java.time.Instant.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "token_expirado_ou_usado"));
        }
        return ResponseEntity.ok(Map.of("message", "token_valido"));
    }

    // POST /usuarios/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body != null ? body.get("token") : null;
        String password = body != null ? body.get("password") : null;
        if (token == null || token.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "token_e_senha_obrigatorios"));
        }

        try {
            String hash = passwordResetService.hashToken(token);
            Optional<PasswordResetToken> rec = passwordResetService.findByTokenHash(hash);
            if (rec.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "token_invalido"));
            PasswordResetToken prt = rec.get();
            if (prt.isUsed() || prt.getExpiresAt().isBefore(java.time.Instant.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "token_expirado_ou_usado"));
            }

            Usuario user = prt.getUsuario();
            // aplicar política mínima de senha (ex.: >= 8)
            if (password.length() < 8) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "senha_pequena"));

            // Passar a senha "raw" para o service; o service irá codificá-la com BCrypt
            UsuarioUpdateDTO usuarioUpdateDTO = new UsuarioUpdateDTO();
            usuarioUpdateDTO.setSenha(password);
            usuarioService.updateUsuario(user.getId(), usuarioUpdateDTO);


            // marcar token como usado
            passwordResetService.markTokenUsed(prt);

            // ideally invalidate sessions / JWTs here (not implemented)

            return ResponseEntity.ok(Map.of("message", "senha_redefinida"));
        } catch (Exception ex) {
            System.err.println("Erro em resetPassword: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "erro_interno"));
        }
    }

    @PostMapping("/{userId}/foto")
    public ResponseEntity<Map<String, Object>> uploadFoto(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, userId);
            UsuarioDTO usuarioAtualizadoDTO = usuarioService.updateFoto(userId, fileUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", usuarioAtualizadoDTO);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Convenience endpoint: upload photo for currently authenticated user (uses JWT)
    @PostMapping("/me/foto")
    public ResponseEntity<Map<String, Object>> uploadFotoMe(@RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Long userId = userPrincipal.getId();
        try {
            String fileUrl = fileStorageService.storeFile(file, userId);
            UsuarioDTO usuarioAtualizadoDTO = usuarioService.updateFoto(userId, fileUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", usuarioAtualizadoDTO);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/conquistas")
    public ResponseEntity<Map<String, Object>> getConquistasDoUsuario(@PathVariable Long userId) {
        Optional<UsuarioDTO> usuarioOpt = usuarioService.getUsuarioById(userId);
        return usuarioOpt
                .map(usuario -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("conquistas", usuario.getConquistas());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Convenience endpoint: get achievements of the authenticated user (uses JWT)
    @GetMapping("/me/conquistas")
    public ResponseEntity<Map<String, Object>> getConquistasDoUsuarioMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Long userId = userPrincipal.getId();
        Optional<UsuarioDTO> usuarioOpt = usuarioService.getUsuarioById(userId);
        return usuarioOpt
                .map(usuario -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("conquistas", usuario.getConquistas());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}