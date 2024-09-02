package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_locais")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "aprovado")
    private boolean aprovado = false;

    @OneToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "tipo_atividade_id")
    private TipoAtividade tipoAtividade;

    public Local(Long id) {
        this.id = id;
    }
}
