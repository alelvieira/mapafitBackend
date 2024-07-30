package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_tipo_atividade")
public class TipoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    // Construtor padrão
    public TipoAtividade() {
    }

    // Construtor com parâmetro para inicializar o ID
    public TipoAtividade(Long id) {
        this.id = id;
    }
}
