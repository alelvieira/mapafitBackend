package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_conquista")
public class Conquista {

    @Id
    private Long id;

    private String titulo;

    private String descricao;

    private String icone;
}