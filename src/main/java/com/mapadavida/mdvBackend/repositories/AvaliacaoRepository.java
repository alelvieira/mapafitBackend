package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Avaliacao;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByUsuarioIdAndLocalId(Long usuarioId, Long localId);
    long countByUsuarioId(Long usuarioId);
    List<Avaliacao> findAllByLocalId(Long localId);

    List<Avaliacao> findAllByUsuarioId(Long usuarioId);
}