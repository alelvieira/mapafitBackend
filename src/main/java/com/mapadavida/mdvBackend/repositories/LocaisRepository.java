package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Local;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocaisRepository extends JpaRepository<Local,Long> {
    Local findByNome(String nome);
    Local findByEndereco(Endereco endereco);
}
