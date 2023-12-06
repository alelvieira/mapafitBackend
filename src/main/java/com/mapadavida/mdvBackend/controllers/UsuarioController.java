package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import com.mapadavida.mdvBackend.services.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioController{

    @Autowired
    private UsuarioRepository usuarioRepository;
    private ModelMapper modelMapper = new ModelMapper();

    private byte[] salt = "MDV".getBytes();

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody Usuario usu) {
        String passwordHash = criptografar(usu.getSenha());

        Usuario usuario = usuarioRepository.findByEmailAndSenha(usu.getEmail(), passwordHash.toString())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        UsuarioDTO usuarioDTO = modelMapper.map(usuario, UsuarioDTO.class);

        return ResponseEntity.status(HttpStatus.OK).body(usuarioDTO);
    }

//    @Autowired
//    private EmailService emailService;
//     @PostMapping
//     public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody Usuario usu) {
    //      if (usu.getTipoUsuario() != TipoUsuario.VISITANTE){//VISITANTE?
//      usu.setTipoUsuario(TipoUsuario.CADASTRADO);
//          String senha = String.valueOf((int)(Math.random() * 9999 + 1000));
    //          String passwordHash = criptografar(senha);
    //      usu.setSenha(passwordHash);
    //      emailService.emailSenha(usu.getEmail(), senha);
    //  }else {
    //      usu.setSenha(criptografar(usu.getSenha()));
//  }
    //      Usuario usuario = usuarioRepository.save(usu);
//
    //      UsuarioDTO usuarioDTO = modelMapper.map(usuario, UsuarioDTO.class);
//
    //      return ResponseEntity.status(HttpStatus.OK).body(usuarioDTO);
    // }

    private String criptografar(String texto){
        MessageDigest digest;
        byte[] passwordHash = null;
        StringBuilder sb = new StringBuilder();
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            passwordHash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            for(int i=0; i < passwordHash.length ;i++)
            {
                sb.append(Integer.toString((passwordHash[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}