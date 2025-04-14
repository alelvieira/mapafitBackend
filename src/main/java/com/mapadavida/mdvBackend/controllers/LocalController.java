package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.services.EnderecoService;
import com.mapadavida.mdvBackend.services.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/local")
public class LocalController {

    private final LocalService localService;
    private final EnderecoService enderecoService;

    @Autowired
    public LocalController(LocalService localService, EnderecoService enderecoService) {
        this.localService = localService;
        this.enderecoService = enderecoService;
    }

    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocais() {
        List<LocalDTO> locais = localService.getLocais();
        return ResponseEntity.ok(locais);
    }

    @GetMapping("/tipo_local/{id}")
    public ResponseEntity<List<LocalDTO>> getLocaisByTipo(@PathVariable Long id) {
        List<LocalDTO> locais = localService.findLocaisByTipoLocal(id);
        return ResponseEntity.ok(locais);
    }

    @GetMapping("/tipo_atividade/{id}")
    public ResponseEntity<List<LocalDTO>> getLocaisByTipoAtividade(@PathVariable Long id) {
        List<LocalDTO> locais = localService.findLocaisByTipoAtividade(id);
        return ResponseEntity.ok(locais);
    }

    @GetMapping("/tipo_acesso/{id}")
    public ResponseEntity<List<LocalDTO>> getLocaisByTipoAcesso(@PathVariable Long id) {
        List<LocalDTO> locais = localService.findLocaisByTipoAcesso(id);
        return ResponseEntity.ok(locais);
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<LocalDTO>> buscarProximos(@RequestParam double latitude,
                                                            @RequestParam double longitude,
                                                            @RequestParam double raio) {
        List<LocalDTO> locais = localService.findLocaisProximos(latitude, longitude, raio);
        return ResponseEntity.ok(locais);
    }

    @PostMapping("/novo")
    public ResponseEntity<EnderecoDTO> criarEndereco(@RequestBody EnderecoDTO enderecoDTO) {
        if (enderecoDTO.getLatitude() == null || enderecoDTO.getLongitude() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Endereco endereco = enderecoService.salvarEndereco(enderecoDTO, enderecoDTO.getLatitude().doubleValue(), enderecoDTO.getLongitude().doubleValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(new EnderecoDTO(endereco));
    }

    @PostMapping
    public ResponseEntity<LocalDTO> createLocal(@RequestBody LocalDTO localDTO) {
        if (localDTO.getEndereco() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (localDTO.getEndereco().getId() == null) {
            Endereco endereco = enderecoService.salvarEndereco(localDTO.getEndereco(),
                    localDTO.getEndereco().getLatitude().doubleValue(),
                    localDTO.getEndereco().getLongitude().doubleValue());
            localDTO.getEndereco().setId(endereco.getId());
        }

        LocalDTO localCriado = localService.createLocal(localDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(localCriado);
    }
}
