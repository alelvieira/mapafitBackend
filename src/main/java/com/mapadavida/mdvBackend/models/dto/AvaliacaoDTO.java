package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Avaliacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoDTO {

    private Long id;
    private String comentario;
    private Integer nota;

    // Optional fields for user and local information (depending on your needs)
    private Long usuarioId;
    private Long localId;

}