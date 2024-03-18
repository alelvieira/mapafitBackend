package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
@Entity
@Table(name = "tb_conquistas")
public class Conquistas {

    @Id
    @Column(name = "nome")
    private String nome;

    @Column(name = "pontuacao")
    private Integer pontuacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
