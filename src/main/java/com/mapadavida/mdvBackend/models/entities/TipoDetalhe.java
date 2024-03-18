package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
@Entity
@Table(name = "tipo_detalhe")
public class TipoDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao")
    private String descricao;
}