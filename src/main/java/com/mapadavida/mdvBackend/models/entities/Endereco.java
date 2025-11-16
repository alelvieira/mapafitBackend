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

import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    // Mapa interno para normalizar nomes completos para siglas (UF)
    private static final Map<String, String> STATE_TO_UF = new HashMap<>();

    static {
        // Preenche com nomes comuns + siglas (tudo em formato normalizado: sem acento e uppercase)
        // Região Sudeste
        STATE_TO_UF.put(normalizeKey("Acre"), "AC");
        STATE_TO_UF.put(normalizeKey("ACRE"), "AC");
        STATE_TO_UF.put(normalizeKey("Alagoas"), "AL");
        STATE_TO_UF.put(normalizeKey("ALAGOAS"), "AL");
        STATE_TO_UF.put(normalizeKey("Amapa"), "AP");
        STATE_TO_UF.put(normalizeKey("AMAPA"), "AP");
        STATE_TO_UF.put(normalizeKey("Amazonas"), "AM");
        STATE_TO_UF.put(normalizeKey("AMAZONAS"), "AM");
        STATE_TO_UF.put(normalizeKey("Bahia"), "BA");
        STATE_TO_UF.put(normalizeKey("BAHIA"), "BA");
        STATE_TO_UF.put(normalizeKey("Ceara"), "CE");
        STATE_TO_UF.put(normalizeKey("CEARA"), "CE");
        STATE_TO_UF.put(normalizeKey("Distrito Federal"), "DF");
        STATE_TO_UF.put(normalizeKey("DISTRITO FEDERAL"), "DF");
        STATE_TO_UF.put(normalizeKey("Espirito Santo"), "ES");
        STATE_TO_UF.put(normalizeKey("ESPIRITO SANTO"), "ES");
        STATE_TO_UF.put(normalizeKey("Goias"), "GO");
        STATE_TO_UF.put(normalizeKey("GOIAS"), "GO");
        STATE_TO_UF.put(normalizeKey("Maranhao"), "MA");
        STATE_TO_UF.put(normalizeKey("MARANHAO"), "MA");
        STATE_TO_UF.put(normalizeKey("Mato Grosso"), "MT");
        STATE_TO_UF.put(normalizeKey("MATO GROSSO"), "MT");
        STATE_TO_UF.put(normalizeKey("Mato Grosso do Sul"), "MS");
        STATE_TO_UF.put(normalizeKey("MATO GROSSO DO SUL"), "MS");
        STATE_TO_UF.put(normalizeKey("Minas Gerais"), "MG");
        STATE_TO_UF.put(normalizeKey("MINAS GERAIS"), "MG");
        STATE_TO_UF.put(normalizeKey("Para"), "PA");
        STATE_TO_UF.put(normalizeKey("PARA"), "PA");
        STATE_TO_UF.put(normalizeKey("Paraiba"), "PB");
        STATE_TO_UF.put(normalizeKey("PARAIBA"), "PB");
        STATE_TO_UF.put(normalizeKey("Parana"), "PR");
        STATE_TO_UF.put(normalizeKey("PARANA"), "PR");
        STATE_TO_UF.put(normalizeKey("Pernambuco"), "PE");
        STATE_TO_UF.put(normalizeKey("PERNAMBUCO"), "PE");
        STATE_TO_UF.put(normalizeKey("Piaui"), "PI");
        STATE_TO_UF.put(normalizeKey("PIAUI"), "PI");
        STATE_TO_UF.put(normalizeKey("Rio de Janeiro"), "RJ");
        STATE_TO_UF.put(normalizeKey("RIO DE JANEIRO"), "RJ");
        STATE_TO_UF.put(normalizeKey("Rio Grande do Norte"), "RN");
        STATE_TO_UF.put(normalizeKey("RIO GRANDE DO NORTE"), "RN");
        STATE_TO_UF.put(normalizeKey("Rio Grande do Sul"), "RS");
        STATE_TO_UF.put(normalizeKey("RIO GRANDE DO SUL"), "RS");
        STATE_TO_UF.put(normalizeKey("Rondonia"), "RO");
        STATE_TO_UF.put(normalizeKey("RONDONIA"), "RO");
        STATE_TO_UF.put(normalizeKey("Roraima"), "RR");
        STATE_TO_UF.put(normalizeKey("RORAIMA"), "RR");
        STATE_TO_UF.put(normalizeKey("Santa Catarina"), "SC");
        STATE_TO_UF.put(normalizeKey("SANTA CATARINA"), "SC");
        STATE_TO_UF.put(normalizeKey("Sao Paulo"), "SP");
        STATE_TO_UF.put(normalizeKey("SAO PAULO"), "SP");
        STATE_TO_UF.put(normalizeKey("Sergipe"), "SE");
        STATE_TO_UF.put(normalizeKey("SERGIPE"), "SE");
        STATE_TO_UF.put(normalizeKey("Tocantins"), "TO");
        STATE_TO_UF.put(normalizeKey("TOCANTINS"), "TO");

        // Também mapeia as próprias siglas para si mesmas (caso o usuário já forneça a UF)
        String[] ufs = {"AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};
        for (String uf : ufs) {
            STATE_TO_UF.put(normalizeKey(uf), uf);
        }
    }

    private static String normalizeKey(String input) {
        if (input == null) return null;
        String noAccents = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return noAccents.trim().toUpperCase(Locale.ROOT);
    }

    private static String resolveUf(String input) {
        if (input == null) return null;
        String key = normalizeKey(input);
        return STATE_TO_UF.getOrDefault(key, null);
    }

    // Método público utilitário para obter a sigla UF (pode ser usado por DTOs/serviços)
    public static String toUf(String input) {
        if (input == null) return null;
        String uf = resolveUf(input);
        if (uf != null) return uf;
        String candidate = input.trim().toUpperCase(Locale.ROOT);
        if (candidate.length() > 2) candidate = candidate.substring(0, 2);
        return candidate;
    }

    // Normaliza o estado para uma UF conhecida; retorna null se não for mapeável (comportamento semelhante ao antigo StateUtils)
    public static String normalizeEstado(String input) {
        return resolveUf(input);
    }

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

    // Substitui a atribuição direta do estado para garantir que armazenamos apenas a sigla (UF)
    public void setEstado(String estado) {
        if (estado == null) {
            this.estado = null;
            return;
        }
        String uf = resolveUf(estado);
        if (uf != null) {
            this.estado = uf;
        } else {
            // Se não conseguimos resolver para uma UF conhecida, ainda tentamos guardar a forma uppercase curta
            String candidate = estado.trim().toUpperCase(Locale.ROOT);
            if (candidate.length() > 2) {
                // reduz para os 2 primeiros caracteres como fallback (não ideal, mas evita salvar strings longas)
                candidate = candidate.substring(0, 2);
            }
            this.estado = candidate;
        }
    }

    public Endereco(EnderecoDTO enderecoDTO) {
        if (enderecoDTO != null) {
            this.id = enderecoDTO.getId();
            this.rua = enderecoDTO.getRua();
            this.cidade = enderecoDTO.getCidade();
            // Normaliza o estado para a sigla
            this.setEstado(enderecoDTO.getEstado());
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
