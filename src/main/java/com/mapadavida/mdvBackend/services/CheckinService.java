package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.CheckinDTO;
import com.mapadavida.mdvBackend.models.entities.Checkin;
import com.mapadavida.mdvBackend.models.entities.Local;
import com.mapadavida.mdvBackend.models.entities.TipoAtividade;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.CheckinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckinService {

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LocalService localService;

    @Autowired
    private TipoAtividadeService tipoAtividadeService;

    @Transactional
    public CheckinDTO performCheckIn(Long userId, Long localId, Long tipoAtividadeId) {
        // Fetching entities. The orElseThrow will handle the case where an entity is not found.
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + userId));

        Local local = localService.getLocalById(localId)
                .orElseThrow(() -> new EntityNotFoundException("Local não encontrado com ID: " + localId));

        TipoAtividade tipoAtividade = tipoAtividadeService.findById(tipoAtividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Atividade não encontrado com ID: " + tipoAtividadeId));

        // This fixes the double-save issue and correctly sets the start time.
        Checkin checkin = new Checkin();
        checkin.setUsuario(usuario);
        checkin.setLocal(local);
        checkin.setTipoAtividade(tipoAtividade);
        checkin.setInicio(LocalDateTime.now());

        Checkin savedCheckin = checkinRepository.save(checkin);

        return new CheckinDTO(savedCheckin);
    }

    @Transactional
    public CheckinDTO performCheckOut(Long checkinId) {
        Checkin checkin = checkinRepository.findById(checkinId)
                .orElseThrow(() -> new EntityNotFoundException("Checkin não encontrado com ID: " + checkinId));

        checkin.setFim(LocalDateTime.now());
        Checkin updatedCheckin = checkinRepository.save(checkin);

        return new CheckinDTO(updatedCheckin);
    }

    @Transactional
    public List<CheckinDTO> findCheckIns(Long userId) {
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        List<Checkin> checkins = checkinRepository.findByUsuarioId(usuario.getId());
        return checkins.stream().map(CheckinDTO::new).toList();
    }

}