package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "tb_locais")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;


    @OneToMany(mappedBy = "local")
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "local")
    private List<Checkin> checkins;

}

