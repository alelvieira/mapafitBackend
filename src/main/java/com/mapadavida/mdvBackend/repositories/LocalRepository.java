package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalRepository extends JpaRepository<Local,Long> {
    Local findByNome(String nome);

    Local findByEndereco(Endereco endereco);

    List<Local> findByTipoLocalId(Long id);

    List<Local> findByTipoAtividadeId(Long id);

    List<Local> findByTipoAcessoId(Long id);

    List<Local> findByEnderecoId(Long id);
}
