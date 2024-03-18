package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco,Long> {
    Endereco findEnderecoByCep(String cep);
}
