package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.AvaliacaoDTO;
import com.mapadavida.mdvBackend.models.dto.AvaliacaoUpdateDTO;
import com.mapadavida.mdvBackend.models.entities.Avaliacao;
import com.mapadavida.mdvBackend.models.entities.Local;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.AvaliacaoRepository;
import com.mapadavida.mdvBackend.repositories.LocalRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mapadavida.mdvBackend.models.dto.AvaliacaoResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LocalRepository localRepository;

    @Transactional
    public AvaliacaoResponseDTO create(AvaliacaoDTO dto) {
        avaliacaoRepository.findByUsuarioIdAndLocalId(dto.getUsuarioId(), dto.getLocalId())
                .ifPresent(a -> {
                     throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já possui uma avaliação para este local.");
                });

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + dto.getUsuarioId()));

        Local local = localRepository.findById(dto.getLocalId())
                .orElseThrow(() -> new EntityNotFoundException("Local não encontrado com id: " + dto.getLocalId()));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setLocal(local);
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        return new AvaliacaoResponseDTO(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public AvaliacaoResponseDTO update(Long id, AvaliacaoUpdateDTO dto) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com id: " + id));

        if (dto.getNota() != null) {
            avaliacao.setNota(dto.getNota());
        }
        if (dto.getComentario() != null) {
            avaliacao.setComentario(dto.getComentario());
        }

        return new AvaliacaoResponseDTO(avaliacaoRepository.save(avaliacao));
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO findById(Long id) {
        return avaliacaoRepository.findById(id)
                .map(AvaliacaoResponseDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com id: " + id));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> findAll() {
        return avaliacaoRepository.findAll().stream()
                .map(AvaliacaoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> findByLocal(Long localId) {
        return avaliacaoRepository.findAllByLocalId(localId).stream().map(AvaliacaoResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> findByUsuario(Long usuarioId) {
        return avaliacaoRepository.findAllByUsuarioId(usuarioId).stream().map(AvaliacaoResponseDTO::new).collect(Collectors.toList());
    }

}