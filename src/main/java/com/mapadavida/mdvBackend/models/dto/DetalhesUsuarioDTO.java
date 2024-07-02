package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalhesUsuarioDTO {

    private DetalhesUsuarioIdDTO id;  // DTO for the embedded ID

    // Optional fields for user and detail type information (depending on your needs)
    private Long usuarioId;
    private Long tipoDetalheId;
}
