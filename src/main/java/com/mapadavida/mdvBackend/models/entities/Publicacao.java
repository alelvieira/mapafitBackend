package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_publicacao")
public class Publicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tipo_publicacao")
    private TipoPublicacao tipoPublicacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "id_img")
    private Integer idImg;

    @ManyToOne
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;}
