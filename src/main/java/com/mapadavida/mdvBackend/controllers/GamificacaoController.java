package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.EventoDTO;
import com.mapadavida.mdvBackend.services.GamificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventos")
@CrossOrigin
public class GamificacaoController {

    @Autowired
    private GamificacaoService gamificacaoService;

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrarEvento(@RequestBody EventoDTO eventoDTO) {
        gamificacaoService.processarEvento(eventoDTO);
        return ResponseEntity.ok().build();
    }
}