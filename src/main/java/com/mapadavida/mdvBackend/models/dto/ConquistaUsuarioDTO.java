package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConquistaUsuarioDTO {

    private Long id;

    private Long usuarioId;
    private Long conquistaPontuacaoId;
    private Long conquistaId; // novo campo para aceitar explicitamente o id da `tb_conquista`
}
