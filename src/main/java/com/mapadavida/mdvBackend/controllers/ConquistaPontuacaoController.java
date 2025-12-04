package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.ConquistaPontuacaoDTO;
import com.mapadavida.mdvBackend.models.dto.ConquistasDTO;
import com.mapadavida.mdvBackend.models.dto.ConquistaUsuarioDTO;
import com.mapadavida.mdvBackend.services.ConquistaPontuacaoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/conquistas-pontuacao")
@CrossOrigin
public class ConquistaPontuacaoController {

    @Autowired
    private ConquistaPontuacaoService service;

    @PostMapping
    public ResponseEntity<ConquistaPontuacaoDTO> create(@RequestBody ConquistaPontuacaoDTO dto) {
        ConquistaPontuacaoDTO created = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PostMapping("/assign")
    public ResponseEntity<ConquistaPontuacaoDTO> assignToUser(@RequestBody ConquistaUsuarioDTO dto) {
        try {
            // Aqui esperamos explicitamente o id da `tb_conquista` (conquistaId). Evitamos usar conquistaPontuacaoId porque
            // esse último se refere a registros já existentes na tabela de pontuações e pode gerar confusão.
            if (dto.getConquistaId() == null || dto.getUsuarioId() == null) {
                return ResponseEntity.badRequest().build();
            }
            ConquistaPontuacaoDTO created = service.createForUser(dto.getUsuarioId(), dto.getConquistaId());
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(uri).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConquistaPontuacaoDTO> update(@PathVariable Long id, @RequestBody ConquistaPontuacaoDTO dto) {
        try {
            ConquistaPontuacaoDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConquistaPontuacaoDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ConquistaPontuacaoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Popula defaults se não existirem
    @PostMapping("/seed")
    public ResponseEntity<List<ConquistaPontuacaoDTO>> seedDefaults() {
        List<ConquistaPontuacaoDTO> created = service.seedDefaults();
        return ResponseEntity.ok(created);
    }

    // Cria várias conquistas de uma vez
    @PostMapping("/bulk")
    public ResponseEntity<List<ConquistaPontuacaoDTO>> bulkCreate(@RequestBody List<ConquistaPontuacaoDTO> dtos) {
        List<ConquistaPontuacaoDTO> created = service.bulkCreate(dtos);
        return ResponseEntity.ok(created);
    }

    // Endpoint helper para receber ConquistasDTO (nome + pontuação + opcional usuarioId)
    @PostMapping("/from")
    public ResponseEntity<ConquistaPontuacaoDTO> createFromFront(@RequestBody ConquistasDTO dto) {
        ConquistaPontuacaoDTO toCreate = new ConquistaPontuacaoDTO();
        toCreate.setNome(dto.getNome());
        toCreate.setQtdPontos(dto.getPontuacao());
        ConquistaPontuacaoDTO created = service.create(toCreate);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
