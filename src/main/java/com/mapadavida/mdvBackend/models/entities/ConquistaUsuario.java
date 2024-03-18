package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_conquista_usuario")
public class ConquistaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_conquista_pontuacao")
    private ConquistaPontuacao conquistaPontuacao;
}
