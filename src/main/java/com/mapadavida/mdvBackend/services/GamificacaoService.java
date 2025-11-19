package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.EventoDTO;
import com.mapadavida.mdvBackend.models.entities.Conquista;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.AvaliacaoRepository;
import com.mapadavida.mdvBackend.repositories.ConquistaRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GamificacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ConquistaRepository conquistaRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    // IDs das conquistas (hardcoded por simplicidade, idealmente viriam de uma config)
    private static final long ID_CONQUISTA_PRIMEIRA_AVALIACAO = 5L; // "Crítico Construtivo"

    @Transactional
    public void processarEvento(EventoDTO evento) {
        Usuario usuario = usuarioRepository.findById(evento.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + evento.getUsuarioId()));

        switch (evento.getTipo()) {
            case "AVALIACAO_CONCLUIDA":
                processarEventoAvaliacao(usuario);
                break;
            // Outros casos: "CHECKIN_COMPLETO", "PERFIL_COMPLETO", etc.
            default:
                // Tipo de evento não conhecido, não faz nada.
                break;
        }
    }

    private void processarEventoAvaliacao(Usuario usuario) {
        // Regra: Ganha conquista na primeira avaliação
        long totalAvaliacoes = avaliacaoRepository.countByUsuarioId(usuario.getId());

        if (totalAvaliacoes == 1) {
            boolean jaPossui = usuario.getConquistas().stream().anyMatch(c -> c.getId().equals(ID_CONQUISTA_PRIMEIRA_AVALIACAO));
            if (!jaPossui) {
                conquistaRepository.findById(ID_CONQUISTA_PRIMEIRA_AVALIACAO).ifPresent(conquista -> {
                    usuario.getConquistas().add(conquista);
                    usuarioRepository.save(usuario);
                });
            }
        }
    }
}