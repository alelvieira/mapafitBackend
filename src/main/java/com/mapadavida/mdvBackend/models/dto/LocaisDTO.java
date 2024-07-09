package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocaisDTO {

    private Long id;
    private String nome;
    private Long endereco_id_endereco;
    private Long tipo_atividade_id;

    // Optional fields for collections (depending on your needs)
    private List<AvaliacaoDTO> avaliacoes;
    private List<CheckinDTO> checkins;
}
