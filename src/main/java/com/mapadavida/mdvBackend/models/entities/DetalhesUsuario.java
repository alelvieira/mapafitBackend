package com.mapadavida.mdvBackend.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_detalhes_usuario")
public class DetalhesUsuario {

    @EmbeddedId
    private DetalhesUsuarioId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("tipoDetalheId")
    @JoinColumn(name = "id_tipo_detalhe")
    private TipoDetalhe tipoDetalhe;
}
