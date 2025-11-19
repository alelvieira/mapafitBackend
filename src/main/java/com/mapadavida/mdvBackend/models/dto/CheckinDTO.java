package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Checkin;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckinDTO {

    private Long id;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private Long usuarioId;
    private String usuarioNome;
    private Long localId;
    private String localNome;
    private String tipoAtividadeNome;

    public CheckinDTO(Checkin checkin) {
        this.id = checkin.getId();
        this.inicio = checkin.getInicio();
        this.fim = checkin.getFim();
        this.usuarioId = checkin.getUsuario().getId();
        this.usuarioNome = checkin.getUsuario().getNome();
        this.localId = checkin.getLocal().getId();
        this.localNome = checkin.getLocal().getNome();
        this.tipoAtividadeNome = checkin.getTipoAtividade().getNome();
    }
}