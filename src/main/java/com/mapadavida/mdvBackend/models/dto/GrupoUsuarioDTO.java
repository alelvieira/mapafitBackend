package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoUsuarioDTO {

    private GrupoUsuarioIdDTO id;

    // Optional fields for Grupo and Usuario information (depending on your needs)
    private Long grupoId;
    private Long usuarioId;

}
