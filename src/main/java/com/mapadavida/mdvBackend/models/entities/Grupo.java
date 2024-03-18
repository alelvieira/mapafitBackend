package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nome_grupo")
    private String nomeGrupo;

}
