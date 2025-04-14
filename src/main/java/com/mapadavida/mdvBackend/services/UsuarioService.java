package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.LoginDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(UsuarioDTO::new).toList();
    }

    public List<Usuario> getUsuariosByTipo(TipoUsuario tipoUsuario) {
        return usuarioRepository.getUsuarioByTipoUsuario(tipoUsuario);
    }

    public UsuarioDTO updateUsuario(Long id, Usuario usuarioUpdated) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setNome(usuarioUpdated.getNome());
            usuario.setEmail(usuarioUpdated.getEmail());
            usuario.setSexo(usuarioUpdated.getSexo());
            usuario.setIdade(usuarioUpdated.getIdade());
            usuario.setEndereco(usuarioUpdated.getEndereco());
            usuario.setTipoUsuario(usuarioUpdated.getTipoUsuario());
            usuario.setSenha(usuarioUpdated.getSenha());
            usuarioRepository.save(usuario);
            return new UsuarioDTO(usuario);
        } else {
            return null;
        }
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario createUser(Usuario user) {
        return usuarioRepository.save(user);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioDTO login(LoginDTO loginDTO){
        Optional<Usuario> user = usuarioRepository.findByEmailAndSenha(loginDTO.getEmail().toLowerCase(), loginDTO.getSenha());
        return user.map(UsuarioDTO::new).orElse(null);
    }

}
