package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.AvaliacaoDTO;
import com.mapadavida.mdvBackend.models.dto.AvaliacaoResponseDTO;
import com.mapadavida.mdvBackend.models.dto.AvaliacaoUpdateDTO;
import com.mapadavida.mdvBackend.services.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> create(@RequestBody AvaliacaoDTO dto) {
        AvaliacaoResponseDTO novaAvaliacao = avaliacaoService.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaAvaliacao.getId()).toUri();
        return ResponseEntity.created(uri).body(novaAvaliacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> update(@PathVariable Long id, @RequestBody AvaliacaoUpdateDTO dto) {
        AvaliacaoResponseDTO avaliacaoAtualizada = avaliacaoService.update(id, dto);
        return ResponseEntity.ok(avaliacaoAtualizada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> getById(@PathVariable Long id) {
        AvaliacaoResponseDTO avaliacao = avaliacaoService.findById(id);
        return ResponseEntity.ok(avaliacao);
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> findAll(
            @RequestParam(required = false) Long localId,
            @RequestParam(required = false) Long usuarioId) {
        List<AvaliacaoResponseDTO> avaliacoes;
        if (localId != null) {
            avaliacoes = avaliacaoService.findByLocal(localId);
        } else if (usuarioId != null) {
            avaliacoes = avaliacaoService.findByUsuario(usuarioId);
        } else {
            avaliacoes = avaliacaoService.findAll();
        }
        return ResponseEntity.ok(avaliacoes);
    }
}