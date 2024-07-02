package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.EnderecoRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
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
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioController{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;
    private ModelMapper modelMapper = new ModelMapper();

    private byte[] salt = "MDV".getBytes();


    @PostMapping(value = "/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario usu) {
        String passwordHash = criptografar(usu.getSenha());
        System.out.print('\n'+passwordHash+'\n');
        Usuario usuario = usuarioRepository.findByEmail(usu.getEmail()).orElse(null);
        if (usuario != null) {
        System.out.print(usuario.getSenha()+'\n');
            usuario = usuarioRepository.findByEmailAndSenha(usu.getEmail(), passwordHash).orElse(null);
            if (usuario != null) {
                // UsuarioDTO usuarioDTO = modelMapper.map(usuario, UsuarioDTO.class);
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
    //      usu.setSe nha(criptografar(usu.getSenha()));
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

    @PostMapping(value = "/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usu) {
        // Verifica se já existe um usuário com o mesmo e-mail
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usu.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            if (usu.getEndereco() == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            System.out.println(usu.getEndereco());
            enderecoRepository.save(usu.getEndereco());
            usu.setSenha(criptografar(usu.getSenha()));

            Usuario usuarioSalvo = usuarioRepository.save(usu);

            return ResponseEntity.ok(usuarioSalvo);
        }
    }





}