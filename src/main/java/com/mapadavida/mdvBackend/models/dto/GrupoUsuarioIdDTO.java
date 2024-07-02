package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GrupoUsuarioIdDTO implements Serializable {

    private Long grupoId;
    private Long usuarioId;
}
