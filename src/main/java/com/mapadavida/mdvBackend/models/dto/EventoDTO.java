package com.mapadavida.mdvBackend.models.dto;

import lombok.Data;

@Data
public class EventoDTO {
    private Long usuarioId;
    private String tipo; // Ex: "AVALIACAO_CONCLUIDA", "CHECKIN_COMPLETO"
}