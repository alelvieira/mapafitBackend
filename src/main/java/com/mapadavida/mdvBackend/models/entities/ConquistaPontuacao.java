package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;

@Entity
public class ConquistaPontuacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qtd_pontos")
    private Integer qtdPontos;

    @Column(name = "nome")
    private String nome;
}
