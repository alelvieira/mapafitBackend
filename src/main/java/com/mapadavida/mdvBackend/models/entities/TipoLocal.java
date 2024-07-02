package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
@Entity
@Table(name = "tb_tipo_local")
public class TipoLocal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;
}

