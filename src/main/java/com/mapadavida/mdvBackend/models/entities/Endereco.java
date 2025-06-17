package com.mapadavida.mdvBackend.models.entities;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
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

    public Double getLatitude() {
        return localizacao != null ? localizacao.getY() : null;
    }

    public Double getLongitude() {
        return localizacao != null ? localizacao.getX() : null;
    }


    public void setLatitudeLongitude(Double lat, Double lng) {
        if (lat != null && lng != null) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            this.localizacao = gf.createPoint(new Coordinate(lng, lat));
        }
    }

    public Endereco(EnderecoDTO enderecoDTO) {
        if (enderecoDTO != null) {
            this.id = enderecoDTO.getId();
            this.rua = enderecoDTO.getRua();
            this.cidade = enderecoDTO.getCidade();
            this.estado = enderecoDTO.getEstado();
            this.numero = enderecoDTO.getNumero();
            this.cep = enderecoDTO.getCep();

            if (enderecoDTO.getLatitude() != null && enderecoDTO.getLongitude() != null) {
                this.setLatitudeLongitude(
                        enderecoDTO.getLatitude().doubleValue(),
                        enderecoDTO.getLongitude().doubleValue()
                );
            }
        }
    }

    public Endereco(EnderecoDTO enderecoDTO, Point localizacao) {
        this(enderecoDTO);
        this.localizacao = localizacao;
    }

}
