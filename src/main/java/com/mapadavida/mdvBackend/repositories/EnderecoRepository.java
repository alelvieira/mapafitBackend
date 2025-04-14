package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco,Long> {

    @Query(value = "SELECT id_endereco, rua, cidade, estado, numero, cep, " +
            "ST_AsBinary(localizacao) AS localizacao, " + // Convertendo para binário
            "ST_Distance(localizacao, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) AS distancia " +
            "FROM tb_endereco " +
            "WHERE ST_DWithin(localizacao, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :raio)",
            nativeQuery = true)
    List<Object[]> findNearby(@Param("latitude") double latitude,
                              @Param("longitude") double longitude,
                              @Param("raio") double raio);

    Endereco findEnderecoByCepAndNumero(String cep, Integer numero);
}
