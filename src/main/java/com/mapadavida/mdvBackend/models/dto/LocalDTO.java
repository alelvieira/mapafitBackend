package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocalDTO {

    private Long id;
    private String nome;

    // Optional fields for collections (depending on your needs)
    private List<AvaliacaoDTO> avaliacoes;
    private List<CheckinDTO> checkins;
}
