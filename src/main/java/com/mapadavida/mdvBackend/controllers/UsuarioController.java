package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.LoginDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.EnderecoRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import com.mapadavida.mdvBackend.services.EnderecoService;
import com.mapadavida.mdvBackend.services.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.query.sqm.EntityTypeException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ModelMapper modelMapper = new ModelMapper();

    private byte[] salt = "MDV".getBytes();

    private static final java.security.Key SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
            "sua-chave-secreta-super-segura-para-jwt1234567890".getBytes()
    );

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
}
