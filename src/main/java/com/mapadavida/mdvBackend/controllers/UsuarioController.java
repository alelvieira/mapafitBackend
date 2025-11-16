package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.LoginDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.entities.PasswordResetToken;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.EnderecoRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import com.mapadavida.mdvBackend.services.EnderecoService;
import com.mapadavida.mdvBackend.services.UsuarioService;
import com.mapadavida.mdvBackend.services.PasswordResetService;
import com.mapadavida.mdvBackend.services.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.query.sqm.EntityTypeException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;


import static java.time.LocalTime.now;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioController{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    private ModelMapper modelMapper = new ModelMapper();

    private byte[] salt = "MDV".getBytes();

    private static final java.security.Key SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
            "sua-chave-secreta-super-segura-para-jwt1234567890".getBytes()
    );

    @Value("${app.frontend.url:http://localhost:3000}")
    private String appFrontendUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dados, jakarta.servlet.http.HttpServletResponse response) {
        if (dados.getEmail() == null || dados.getSenha() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "dados_invalidos"));
        }

        Usuario usuario = usuarioRepository.findByEmail(dados.getEmail()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "email_nao_encontrado"));
        }

        if (!usuario.getSenha().equals(dados.getSenha()))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "senha_incorreta"));
        }

        // Registra o usuário autenticado no contexto do Spring Security
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            usuario, null, java.util.List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = gerarTokenJwt(usuario);

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // true em produção (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 dia
        response.addCookie(cookie);

        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);
        return ResponseEntity.ok(Map.of("user", usuarioDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(new UsuarioDTO(usuario));
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
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String gerarTokenJwt(Usuario usuario) {
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1 dia
                .signWith(SECRET_KEY, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    @PostMapping(value = "/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usu) {
        Optional<Usuario> usuarioExistente = usuarioService.findByEmail(usu.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            if (usu.getEndereco() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Endereco enderecoNormalizado = enderecoService.findOrSave(usu.getEndereco());
            usu.setEndereco(enderecoNormalizado);

            usu.setSenha(criptografar(usu.getSenha()));
            Usuario usuarioSalvo = usuarioService.createUser(usu);
            return ResponseEntity.ok(usuarioSalvo);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/tipoUsuario/{tipoUsuario}")
    public ResponseEntity<List<Usuario>> getUsuariosByTipo(@PathVariable TipoUsuario tipoUsuario) {
        try {
            List<Usuario> usuarios = usuarioService.getUsuariosByTipo(tipoUsuario);
            if (usuarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(usuarios);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        UsuarioDTO updatedUsuario = usuarioService.updateUsuario(id, usuarioDetails);
        if (updatedUsuario != null) {
            return ResponseEntity.ok(updatedUsuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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

            // criptografar senha (mantendo padrão do projeto - criptografar())
            String nova = criptografar(password);
            user.setSenha(nova);
            usuarioService.updateUsuario(user.getId(), user);

            // marcar token como usado
            passwordResetService.markTokenUsed(prt);

            // ideally invalidate sessions / JWTs here (not implemented)

            return ResponseEntity.ok(Map.of("message", "senha_redefinida"));
        } catch (Exception ex) {
            System.err.println("Erro em resetPassword: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "erro_interno"));
        }
    }
}
