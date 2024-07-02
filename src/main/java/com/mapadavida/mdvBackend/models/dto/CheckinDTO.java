package com.mapadavida.mdvBackend.models.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CheckinDTO {

    private Long id;

    // Optional fields for user, local, and activity information (depending on your needs)
    private Long usuarioId;
    private Long localId;
    private Long tipoAtividadeId;

    private LocalDateTime inicio;
    private LocalDateTime fim;
}
