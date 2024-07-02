package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DetalhesUsuarioIdDTO implements Serializable {

    private Long usuarioId;
    private Long tipoDetalheId;
}
