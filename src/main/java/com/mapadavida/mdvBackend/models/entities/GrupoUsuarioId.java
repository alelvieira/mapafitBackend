package com.mapadavida.mdvBackend.models.entities;

import java.io.Serializable;
import jakarta.persistence.*;

@Embeddable
public class GrupoUsuarioId implements Serializable {

    @Column(name = "id_grupo")
    private Long grupoId;

    @Column(name = "id_usuario")
    private Long usuarioId;}
