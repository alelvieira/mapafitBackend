package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_grupo_usuario")
public class GrupoUsuario {

    @EmbeddedId
    private GrupoUsuarioId id;

    @ManyToOne
    @MapsId("grupoId")
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
