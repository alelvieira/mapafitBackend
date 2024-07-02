package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoDTO {

    private Long id;

    // Optional field for user information (depending on your needs)
    private Long usuarioId;
    private String nomeGrupo;
}
