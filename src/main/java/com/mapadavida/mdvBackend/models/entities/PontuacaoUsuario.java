package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_pontuacao_usuario")
public class PontuacaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "pontuacao_anterior")
    private Integer pontuacaoAnterior;

    @Column(name = "pontuacao_atual")
    private Integer pontuacaoAtual;}
