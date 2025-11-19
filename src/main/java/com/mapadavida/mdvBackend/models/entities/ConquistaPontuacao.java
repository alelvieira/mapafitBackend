package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_conquista_pontuacao")
public class ConquistaPontuacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qtd_pontos")
    private Integer qtdPontos;

    @Column(name = "nome")
    private String nome;
}
