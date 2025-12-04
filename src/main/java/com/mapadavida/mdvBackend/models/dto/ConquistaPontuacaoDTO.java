package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConquistaPontuacaoDTO {

    private Long id;
    private Integer qtdPontos;
    private String nome;
    private Long usuarioId;
    private Long conquistaId;
    private LocalDateTime dataAlcancada;
}
