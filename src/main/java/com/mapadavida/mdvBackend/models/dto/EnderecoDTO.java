package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EnderecoDTO {
    private Long id;
    private String rua;
    private String cidade;
    private String estado;
    private String cep;
    private Long latitude;
    private Long longitude;
    private LocalDateTime dataCriacao;

}