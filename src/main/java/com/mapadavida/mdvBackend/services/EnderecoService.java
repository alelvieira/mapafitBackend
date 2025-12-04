package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.repositories.EnderecoRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public List<Endereco> getAllEnderecos() {
        return enderecoRepository.findAll();
    }

    public Optional<Endereco> getEnderecoById(Long id) {
        return enderecoRepository.findById(id);
    }

    public Endereco createEndereco(Endereco endereco) {
        endereco.setData_criacao(LocalDateTime.now());
        return enderecoRepository.save(endereco);
    }

    public List<EnderecoDTO> buscarProximos(double latitude, double longitude, double raio) {
        WKBReader wkbReader = new WKBReader(); // Para converter bytes em Point

        return enderecoRepository.findNearby(latitude, longitude, raio)
                .stream()
                .map(obj -> {
                    try {
                        // Converte o array de bytes em um Point
                        byte[] bytes = (byte[]) obj[6]; // localizacao
                        Point localizacao = (Point) wkbReader.read(bytes);

                        // Cria o EnderecoDTO
                        return new EnderecoDTO(
                                ((Number) obj[0]).longValue(),  // ID
                                (String) obj[1],  // Rua
                                (String) obj[2],  // Cidade
                                (String) obj[3],  // Estado
                                ((Number) obj[4]).intValue(),  // Número
                                (String) obj[5],  // CEP
                                BigDecimal.valueOf(localizacao.getY()), // Latitude
                                BigDecimal.valueOf(localizacao.getX()) // Longitude
                        );


                    } catch (ParseException e) {
                        throw new RuntimeException("Erro ao converter localização", e);
                    }
                })
                .collect(Collectors.toList());
    }

    public Endereco salvarEndereco(EnderecoDTO enderecoDTO, double latitude, double longitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude)); // Ordem correta: longitude, latitude
        Endereco endereco = new Endereco(enderecoDTO, point);
        enderecoRepository.save(endereco);
        return endereco;
    }

    /**
     * Calcula a distância em metros entre dois pontos geográficos usando a fórmula de Haversine
     * @param latitude1 Latitude do primeiro ponto em graus decimais
     * @param longitude1 Longitude do primeiro ponto em graus decimais
     * @param enderecoDTO Endereço do segundo ponto
     * @return Distância em metros (arredondada para o inteiro mais próximo)
     */
    public Integer calcularDistancia(double latitude1, double longitude1, EnderecoDTO enderecoDTO) {
        double latitude2 = enderecoDTO.getLatitude().doubleValue();
        double longitude2 = enderecoDTO.getLongitude().doubleValue();

        // Raio da Terra em metros
        final int R = 6371000;

        // Converter graus para radianos
        double lat1 = Math.toRadians(latitude1);
        double lon1 = Math.toRadians(longitude1);
        double lat2 = Math.toRadians(latitude2);
        double lon2 = Math.toRadians(longitude2);

        // Diferenças nas coordenadas
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Fórmula de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(lat1) * Math.cos(lat2) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distância em metros
        double distance = R * c;

        return (int) Math.round(distance);
    }

    public Endereco findOrSave(Endereco endereco) {
        return enderecoRepository
                .findByRuaAndNumeroAndCidadeAndEstadoAndCep(
                        endereco.getRua(),
                        endereco.getNumero(),
                        endereco.getCidade(),
                        endereco.getEstado(),
                        endereco.getCep()
                )
                .orElseGet(() -> enderecoRepository.save(endereco));
    }
}