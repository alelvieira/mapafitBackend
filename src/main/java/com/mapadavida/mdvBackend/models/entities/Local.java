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

    @ManyToOne
    @JoinColumn(name = "tipo_acesso_id")
    private TipoAcesso tipoAcesso;

    @Column(name = "horarios_funcionamento")
    private String horarios_funcionamento;

    @Column(name = "informacoes_adicionais")
    private String informacoes_adicionais;

}
