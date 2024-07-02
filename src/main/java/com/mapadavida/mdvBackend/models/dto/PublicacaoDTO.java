package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicacaoDTO {

    private Long id;

    // Optional fields for related entities (depending on your needs)
    private Long tipoPublicacaoId;
    private Long usuarioId;
    private Integer idImg;
    private Long grupoId;
}
