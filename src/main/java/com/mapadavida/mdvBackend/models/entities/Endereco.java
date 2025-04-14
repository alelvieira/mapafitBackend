package com.mapadavida.mdvBackend.models.entities;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tb_endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Long id;

    @Column(name = "data_criacao", columnDefinition = "TIMESTAMP")
    private LocalDateTime data_criacao;

    @NotBlank(message = "A rua não pode estar em branco")
    private String rua;

    @NotBlank(message = "A cidade não pode estar em branco")
    private String cidade;

    @NotBlank(message = "O estado não pode estar em branco")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres")
    private String estado;

    @NotNull(message = "O número não pode ser nulo")
    private Integer numero;

    @NotBlank(message = "O CEP não pode estar em branco")
    @Size(min = 8, max = 8, message = "O CEP deve ter 8 caracteres")
    private String cep;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point localizacao;

    public Endereco(EnderecoDTO enderecoDTO, Point localizacao) {
        if (enderecoDTO != null) {
            this.id = enderecoDTO.getId();
            this.rua = enderecoDTO.getRua();
            this.cidade = enderecoDTO.getCidade();
            this.estado = enderecoDTO.getEstado();
            this.numero = enderecoDTO.getNumero();
            this.cep = enderecoDTO.getCep();
            this.localizacao = localizacao;
        }
    }
}
