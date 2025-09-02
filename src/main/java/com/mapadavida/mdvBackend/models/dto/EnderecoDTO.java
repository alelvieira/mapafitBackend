package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EnderecoDTO {
    private Long id;
    private String rua;
    private String cidade;
    private String estado;
    private Integer numero;
    private String cep;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public EnderecoDTO(Endereco endereco) {
        if (endereco != null) {
            this.id = endereco.getId();
            this.rua = endereco.getRua();
            this.cidade = endereco.getCidade();
            this.estado = endereco.getEstado();
            this.numero = endereco.getNumero();
            this.cep = endereco.getCep();

            // Extração da latitude e longitude a partir do objeto Point
            if (endereco.getLocalizacao() != null) {
                this.latitude = BigDecimal.valueOf(endereco.getLocalizacao().getY());
                this.longitude = BigDecimal.valueOf(endereco.getLocalizacao().getX());
            }
        }
    }

    public EnderecoDTO(long id, String rua, String cidade, String estado, int numero, String cep, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.rua = rua;
        this.cidade = cidade;
        this.estado = estado;
        this.numero = numero;
        this.cep = cep;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
