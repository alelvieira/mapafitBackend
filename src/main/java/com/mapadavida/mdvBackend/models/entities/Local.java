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

    @OneToMany(mappedBy = "local")
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "local")
    private List<Checkin> checkins;

    @Column(name = "aprovado")
    private boolean aprovado = false;

    @OneToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "id_tipo_atividade")
    private TipoAtividade tipoAtividade;

    // Construtor padrão
    public Local() {
    }

    // Construtor com parâmetro para inicializar o ID
    public Local(Long id) {
        this.id = id;
    }
}
