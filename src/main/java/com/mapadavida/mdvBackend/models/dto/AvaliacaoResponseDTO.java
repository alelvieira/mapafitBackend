package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Avaliacao;
import lombok.Data;

@Data
public class AvaliacaoResponseDTO {
    private Long id;
    private Integer nota;
    private String comentario;
    private Long usuarioId;
    private String usuarioNome;
    private Long localId;
    private String localNome;

    public AvaliacaoResponseDTO(Avaliacao avaliacao) {
        this.id = avaliacao.getId();
        this.nota = avaliacao.getNota();
        this.comentario = avaliacao.getComentario();
        this.usuarioId = avaliacao.getUsuario().getId();
        this.usuarioNome = avaliacao.getUsuario().getNome();
        this.localId = avaliacao.getLocal().getId();
        this.localNome = avaliacao.getLocal().getNome();
    }
}