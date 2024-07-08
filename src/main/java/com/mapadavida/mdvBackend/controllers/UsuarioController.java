package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.EnderecoRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import com.mapadavida.mdvBackend.services.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.query.sqm.EntityTypeException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UsuarioService usuarioService;

    private ModelMapper modelMapper = new ModelMapper();

    private byte[] salt = "MDV".getBytes();

    @PostMapping(value = "/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario usu) {
        String passwordHash = criptografar(usu.getSenha());
        Usuario usuario = usuarioRepository.findByEmail(usu.getEmail()).orElse(null);
        if (usuario != null) {
            usuario = usuarioRepository.findByEmailAndSenha(usu.getEmail(), passwordHash).orElse(null);
            if (usuario != null) {
                String token = UUID.randomUUID().toString();
                usuario.setToken(token);
                usuarioRepository.save(usuario);
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private String criptografar(String texto) {
        MessageDigest digest;
        byte[] passwordHash = null;
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

    @PostMapping(value = "/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usu) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usu.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            if (usu.getEndereco() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            enderecoRepository.save(usu.getEndereco());
            usu.setSenha(criptografar(usu.getSenha()));
            Usuario usuarioSalvo = usuarioRepository.save(usu);
            return ResponseEntity.ok(usuarioSalvo);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
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
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Usuario updatedUsuario = usuarioService.updateUsuario(id, usuarioDetails);
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
}
