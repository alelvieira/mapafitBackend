package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Conquista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConquistaRepository extends JpaRepository<Conquista, Long> {
    // O método findById já é suficiente por enquanto.
}