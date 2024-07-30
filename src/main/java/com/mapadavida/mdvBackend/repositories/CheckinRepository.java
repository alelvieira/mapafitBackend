package com.mapadavida.mdvBackend.repositories;

import com.mapadavida.mdvBackend.models.entities.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Long> {
    // Adicione métodos de consulta personalizados aqui, se necessário

    // Exemplo de consulta personalizada:
    // List<Checkin> findByUsuarioId(Long usuarioId);
}
