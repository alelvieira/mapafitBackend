package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.LoginDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.dto.UsuarioUpdateDTO;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnderecoService enderecoService;

    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", key = "#id")
    public Optional<UsuarioDTO> getUsuarioById(Long id) {
        return usuarioRepository.findById(id).map(UsuarioDTO::new);
    }

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(UsuarioDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> getUsuariosByTipo(TipoUsuario tipoUsuario) {
        return usuarioRepository.getUsuarioByTipoUsuario(tipoUsuario).stream()
                .map(UsuarioDTO::new)
                .toList();
    }

    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "usuarios", key = "#id")
            }
    )
    public UsuarioDTO updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdated) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setNome(usuarioUpdated.getNome());
            usuario.setEmail(usuarioUpdated.getEmail());
            usuario.setSexo(usuarioUpdated.getSexo());
            usuario.setIdade(usuarioUpdated.getIdade());
            usuario.setTelefone(usuarioUpdated.getTelefone());
            usuario.setEndereco(usuarioUpdated.getEndereco());
            usuario.setTipoUsuario(usuarioUpdated.getTipoUsuario());
            if (usuarioUpdated.getSenha() != null && !usuarioUpdated.getSenha().isBlank()) {
                String nova = usuarioUpdated.getSenha();
                if (!isBCrypt(nova)) {
                    nova = passwordEncoder.encode(nova);
                }
                usuario.setSenha(nova);
            }
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
        // encode password if not already encoded
        if (user.getSenha() != null && !user.getSenha().isBlank()) {
            if (!isBCrypt(user.getSenha())) {
                user.setSenha(passwordEncoder.encode(user.getSenha()));
            }
        }
        return usuarioRepository.save(user);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }


    public List<UsuarioDTO> buscarUsuariosPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(UsuarioDTO::new)
                .toList();
    }

    public UsuarioDTO login(LoginDTO loginDTO){
        Optional<Usuario> user = usuarioRepository.findByEmailAndSenha(loginDTO.getEmail().toLowerCase(), loginDTO.getSenha());
        return user.map(UsuarioDTO::new).orElse(null);
    }

    // removed UserDetailsService implementation to avoid duplicate UserDetailsService beans

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "usuarios", key = "#userId")
            }
    )
    public UsuarioDTO updateFoto(Long userId, String fotoUrl) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        usuario.setFotoUrl(fotoUrl);
        return new UsuarioDTO(usuarioRepository.save(usuario));
    }

    /**
     * Limpa o cache de um usuário específico
     * @param userId ID do usuário
     * @param email Email do usuário (opcional, usado para limpar por email)
     */
    @CacheEvict(value = "usuarios", key = "#userId")
    public void clearUserCache(Long userId, String email) {
        // Este método não precisa de implementação, a anotação @CacheEvict fará o trabalho
    }

    /**
     * Limpa todo o cache de usuários
     */
    @CacheEvict(value = "usuarios", allEntries = true)
    public void clearAllUsersCache() {
        // Este método não precisa de implementação, a anotação @CacheEvict fará o trabalho
    }

    private boolean isBCrypt(String s) {
        return s != null && s.matches("\\A\\$2[aby]\\$.{56}\\z");
    }
}
