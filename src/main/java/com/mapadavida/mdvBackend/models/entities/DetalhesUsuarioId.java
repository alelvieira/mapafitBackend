package com.mapadavida.mdvBackend.models.entities;

import java.io.Serializable;
import jakarta.persistence.*;

@Embeddable
public class DetalhesUsuarioId implements Serializable {

    @Column(name = "id_usuario")
    private Long usuarioId;

    @Column(name = "id_tipo_detalhe")
    private Long tipoDetalheId;

}

