package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conquista_pontuacao")
public class ConquistaPontuacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qtd_pontos")
    private Integer qtdPontos;

    @Column(name = "nome")
    private String nome;

    @Column(name = "data_alcancada")
    private LocalDateTime dataAlcancada;

    @ManyToOne
    @JoinColumn(name = "id_conquista")
    private Conquista conquista;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQtdPontos() {
        return qtdPontos;
    }

    public void setQtdPontos(Integer qtdPontos) {
        this.qtdPontos = qtdPontos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataAlcancada() {
        return dataAlcancada;
    }

    public void setDataAlcancada(LocalDateTime dataAlcancada) {
        this.dataAlcancada = dataAlcancada;
    }

    public Conquista getConquista() {
        return conquista;
    }

    public void setConquista(Conquista conquista) {
        this.conquista = conquista;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
