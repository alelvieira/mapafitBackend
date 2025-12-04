package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.ConquistaPontuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConquistaPontuacaoRepository extends JpaRepository<ConquistaPontuacao, Long> {
    Optional<ConquistaPontuacao> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
    // repositório padrão para gerenciar tipos de conquista com pontuação
}
