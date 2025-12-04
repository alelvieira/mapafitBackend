package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.ConquistaPontuacaoDTO;
import com.mapadavida.mdvBackend.models.entities.ConquistaPontuacao;
import com.mapadavida.mdvBackend.models.entities.Conquista;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.ConquistaPontuacaoRepository;
import com.mapadavida.mdvBackend.repositories.ConquistaRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConquistaPontuacaoService {

    @Autowired
    private ConquistaPontuacaoRepository repository;

    @Autowired
    private ConquistaRepository conquistaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public ConquistaPontuacaoDTO create(ConquistaPontuacaoDTO dto) {
        ConquistaPontuacao entity = new ConquistaPontuacao();
        entity.setNome(dto.getNome());
        entity.setQtdPontos(dto.getQtdPontos());
        if (dto.getConquistaId() != null) {
            Conquista c = conquistaRepository.findById(dto.getConquistaId()).orElseThrow(() -> new EntityNotFoundException("Conquista não encontrada: " + dto.getConquistaId()));
            entity.setConquista(c);
        }
        if (dto.getUsuarioId() != null) {
            Usuario u = usuarioRepository.findById(dto.getUsuarioId()).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + dto.getUsuarioId()));
            entity.setUsuario(u);
        }
        entity.setDataAlcancada(dto.getDataAlcancada() == null ? LocalDateTime.now() : dto.getDataAlcancada());
        ConquistaPontuacao saved = repository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public ConquistaPontuacaoDTO createForUser(Long usuarioId, Long conquistaId) {
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usuarioId));
        Conquista c = conquistaRepository.findById(conquistaId).orElseThrow(() -> new EntityNotFoundException("Conquista não encontrada: " + conquistaId));

        ConquistaPontuacao entity = new ConquistaPontuacao();
        entity.setUsuario(u);
        entity.setConquista(c);
        entity.setNome(c.getTitulo());
        // tenta encontrar uma pontuação padrão para essa conquista (por nome) — se existir, usa; senão, usa 0
        repository.findByNomeIgnoreCase(c.getTitulo()).ifPresentOrElse(cp -> entity.setQtdPontos(cp.getQtdPontos()), () -> entity.setQtdPontos(0));
        entity.setDataAlcancada(LocalDateTime.now());
        ConquistaPontuacao saved = repository.save(entity);

        // vincula a conquista no relacionamento ManyToMany do usuário (tb_usuario_conquista)
        boolean jaPossui = u.getConquistas().stream().anyMatch(conq -> conq.getId().equals(c.getId()));
        if (!jaPossui) {
            u.getConquistas().add(c);
            usuarioRepository.save(u);
        }

        return toDTO(saved);
    }

    @Transactional
    public ConquistaPontuacaoDTO update(Long id, ConquistaPontuacaoDTO dto) {
        ConquistaPontuacao entity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("ConquistaPontuacao não encontrada: " + id));
        if (dto.getNome() != null) entity.setNome(dto.getNome());
        if (dto.getQtdPontos() != null) entity.setQtdPontos(dto.getQtdPontos());
        return toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ConquistaPontuacaoDTO findById(Long id) {
        return repository.findById(id).map(this::toDTO).orElseThrow(() -> new EntityNotFoundException("ConquistaPontuacao não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<ConquistaPontuacaoDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("ConquistaPontuacao não encontrada: " + id);
        repository.deleteById(id);
    }

    private ConquistaPontuacaoDTO toDTO(ConquistaPontuacao entity) {
        ConquistaPontuacaoDTO dto = new ConquistaPontuacaoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setQtdPontos(entity.getQtdPontos());
        dto.setDataAlcancada(entity.getDataAlcancada());
        if (entity.getUsuario() != null) dto.setUsuarioId(entity.getUsuario().getId());
        if (entity.getConquista() != null) dto.setConquistaId(entity.getConquista().getId());
        return dto;
    }

    // Método para criar várias conquistas de uma vez (usando saveAll)
    @Transactional
    public List<ConquistaPontuacaoDTO> bulkCreate(List<ConquistaPontuacaoDTO> dtos) {
        List<ConquistaPontuacao> entities = dtos.stream().map(d -> {
            ConquistaPontuacao e = new ConquistaPontuacao();
            e.setNome(d.getNome());
            e.setQtdPontos(d.getQtdPontos());
            return e;
        }).collect(Collectors.toList());
        List<ConquistaPontuacao> saved = repository.saveAll(entities);
        return saved.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Método para popular conquistas padrão se não existirem
    @Transactional
    public List<ConquistaPontuacaoDTO> seedDefaults() {
        List<ConquistaPontuacaoDTO> defaults = Arrays.asList(
                createIfNotExists("Primeira Avaliação", 50),
                createIfNotExists("Perfil Completo", 100),
                createIfNotExists("10 Avaliações", 500),
                createIfNotExists("Check-in Diário", 10),
                createIfNotExists("Crítico Construtivo", 25)
        );
        return defaults.stream().filter(d -> d != null).collect(Collectors.toList());
    }

    private ConquistaPontuacaoDTO createIfNotExists(String nome, Integer pontos) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            return repository.findByNomeIgnoreCase(nome).map(this::toDTO).orElse(null);
        }
        ConquistaPontuacao entity = new ConquistaPontuacao();
        entity.setNome(nome);
        entity.setQtdPontos(pontos);
        ConquistaPontuacao saved = repository.save(entity);
        return toDTO(saved);
    }
}
