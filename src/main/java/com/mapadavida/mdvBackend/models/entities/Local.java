package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
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

    @Column(name = "aprovado")
    private boolean aprovado = false;

    @OneToOne
    private Endereco endereco;

    @ManyToOne
    private TipoAtividade tipoAtividade;
}

