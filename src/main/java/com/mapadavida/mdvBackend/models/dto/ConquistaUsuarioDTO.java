package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConquistaUsuarioDTO {

    private Long id;

    private Long usuarioId;
    private Long conquistaPontuacaoId;
}
