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
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalTime.now;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private UsuarioService usuarioService;

    private ModelMapper modelMapper = new ModelMapper();

    private byte[] salt = "MDV".getBytes();

    @PostMapping(value = "/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginDTO usu) {
        if (usu.getEmail() != null && usu.getSenha() != null) {
            String passwordHash = criptografar(usu.getSenha());
            usu.setSenha(passwordHash);
            UsuarioDTO usuarioDTO = usuarioService.login(usu);
            if (usuarioDTO != null) {
                return ResponseEntity.ok(usuarioDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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

    @PostMapping(value = "/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usu) {
        Optional<Usuario> usuarioExistente = usuarioService.findByEmail(usu.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            if (usu.getEndereco() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            enderecoService.createEndereco(usu.getEndereco());
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
}
