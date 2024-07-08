package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> getUsuariosByTipo(TipoUsuario tipoUsuario) {
        return usuarioRepository.getUsuarioByTipoUsuario(tipoUsuario);
    }

    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setNome(usuarioDetails.getNome());
            usuario.setEmail(usuarioDetails.getEmail());
            usuario.setSexo(usuarioDetails.getSexo());
            usuario.setToken(usuarioDetails.getToken());
            usuario.setIdade(usuarioDetails.getIdade());
            usuario.setEndereco(usuarioDetails.getEndereco());
            usuario.setTipoUsuario(usuarioDetails.getTipoUsuario());
            usuario.setSenha(usuarioDetails.getSenha());
            return usuarioRepository.save(usuario);
        } else {
            return null;
        }
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
