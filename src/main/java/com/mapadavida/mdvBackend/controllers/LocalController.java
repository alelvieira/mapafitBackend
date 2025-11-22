package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import com.mapadavida.mdvBackend.models.entities.*;
import com.mapadavida.mdvBackend.repositories.TipoAcessoRepository;
import com.mapadavida.mdvBackend.repositories.TipoAtividadeRepository;
import com.mapadavida.mdvBackend.repositories.TipoLocalRepository;
import com.mapadavida.mdvBackend.services.EnderecoService;
import com.mapadavida.mdvBackend.services.LocalService;
import com.mapadavida.mdvBackend.services.TipoAcessoService;
import com.mapadavida.mdvBackend.services.TipoAtividadeService;
import com.mapadavida.mdvBackend.services.TipoLocalService;
import com.mapadavida.mdvBackend.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/locais")
public class LocalController {

    @Value("${GOOGLE_API_KEY:${google.api.key:}}")
    private String googleApiKey; // kept for backward-compatibility in properties if needed elsewhere

    private final LocalService localService;
    private final EnderecoService enderecoService;
    private final GeocodingService geocodingService;

    @Autowired private TipoLocalRepository tipoLocalRepository;
    @Autowired private TipoAtividadeRepository tipoAtividadeRepository;
    @Autowired private TipoAcessoRepository tipoAcessoRepository;

    @Autowired private TipoLocalService tipoLocalService;
    @Autowired private TipoAtividadeService tipoAtividadeService;
    @Autowired private TipoAcessoService tipoAcessoService;

    @Autowired
    public LocalController(LocalService localService, EnderecoService enderecoService, GeocodingService geocodingService) {
        this.localService = localService;
        this.enderecoService = enderecoService;
        this.geocodingService = geocodingService;
    }

    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocais(
            @RequestParam(name = "latitude", required = false) Double latitude,
            @RequestParam(name = "longitude", required = false) Double longitude) {

        if ((latitude == null && longitude != null) || (latitude != null && longitude == null)) {
            return ResponseEntity.badRequest().build();
        }

        List<LocalDTO> locais = localService.getLocais(latitude, longitude);
        return ResponseEntity.ok(locais);
    }

    @GetMapping("/aprovados")
    public List<Local> getAprovados() {
        return localService.listarAprovados();
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

        Endereco endereco = enderecoService.salvarEndereco(enderecoDTO,
                enderecoDTO.getLatitude().doubleValue(),
                enderecoDTO.getLongitude().doubleValue());

        return ResponseEntity.status(HttpStatus.CREATED).body(new EnderecoDTO(endereco));
    }

    @PostMapping
    public ResponseEntity<LocalDTO> createLocal(@RequestBody LocalDTO localDTO) {
        System.out.println(">>> Recebido novo local: " + localDTO.getNome());

        if (localDTO.getEndereco() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // normalizar e validar estado antes de persistir
        if (localDTO.getEndereco().getEstado() != null) {
            String estadoNorm = Endereco.normalizeEstado(localDTO.getEndereco().getEstado());
            if (estadoNorm == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            localDTO.getEndereco().setEstado(estadoNorm);
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

    @PutMapping("/{id}")
    public ResponseEntity<LocalDTO> updateLocal(@PathVariable Long id, @RequestBody LocalDTO dto) {
        // normalizar estado com utilitário
        if (dto != null && dto.getEndereco() != null && dto.getEndereco().getEstado() != null) {
            dto.getEndereco().setEstado(com.mapadavida.mdvBackend.models.entities.Endereco.normalizeEstado(dto.getEndereco().getEstado()));
            if (dto.getEndereco().getEstado() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        return ResponseEntity.ok(localService.updateLocal(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocal(@PathVariable Long id) {
        localService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/geocode")
    public ResponseEntity<?> geocode(@RequestParam String endereco) {
        return geocodingService.geocode(endereco);
    }

    @DeleteMapping("/tipo-local/{id}")
    public ResponseEntity<Void> deleteTipoLocal(@PathVariable Long id) {
        if (!tipoLocalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tipoLocalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tipo-atividade/{id}")
    public ResponseEntity<Void> deleteTipoAtividade(@PathVariable Long id) {
        if (!tipoAtividadeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tipoAtividadeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tipo-acesso/{id}")
    public ResponseEntity<Void> deleteTipoAcesso(@PathVariable Long id) {
        var tipoAcesso = tipoAcessoRepository.findById(id);
        if (tipoAcesso.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tipoAcessoService.delete(tipoAcesso.get());
        return ResponseEntity.noContent().build();
    }

    // Adicionados endpoints CRUD para TipoLocal, TipoAtividade e TipoAcesso (POST, PUT, GET por id)

    // TipoLocal
    @PostMapping("/tipo-local")
    public ResponseEntity<TipoLocal> createTipoLocal(@RequestBody TipoLocal tipoLocal) {
        TipoLocal saved = tipoLocalService.save(tipoLocal);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/tipo-local/{id}")
    public ResponseEntity<TipoLocal> updateTipoLocal(@PathVariable Long id, @RequestBody TipoLocal tipoLocal) {
        if (!tipoLocalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        TipoLocal updated = tipoLocalService.update(id, tipoLocal);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/tipo-local/{id}")
    public ResponseEntity<TipoLocal> getTipoLocalById(@PathVariable Long id) {
        return tipoLocalService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // TipoAtividade
    @PostMapping("/tipo-atividade")
    public ResponseEntity<TipoAtividade> createTipoAtividade(@RequestBody TipoAtividade tipoAtividade) {
        TipoAtividade saved = tipoAtividadeService.save(tipoAtividade);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/tipo-atividade/{id}")
    public ResponseEntity<TipoAtividade> updateTipoAtividade(@PathVariable Long id, @RequestBody TipoAtividade tipoAtividade) {
        if (!tipoAtividadeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        TipoAtividade updated = tipoAtividadeService.update(id, tipoAtividade);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/tipo-atividade/{id}")
    public ResponseEntity<TipoAtividade> getTipoAtividadeById(@PathVariable Long id) {
        return tipoAtividadeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // TipoAcesso
    @PostMapping("/tipo-acesso")
    public ResponseEntity<TipoAcesso> createTipoAcesso(@RequestBody TipoAcesso tipoAcesso) {
        TipoAcesso saved = tipoAcessoService.save(tipoAcesso);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/tipo-acesso/{id}")
    public ResponseEntity<TipoAcesso> updateTipoAcesso(@PathVariable Long id, @RequestBody TipoAcesso tipoAcesso) {
        if (!tipoAcessoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // reutiliza save/update do service: implementa atualização simples
        var existing = tipoAcessoService.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        TipoAcesso toSave = existing.get();
        toSave.setNome(tipoAcesso.getNome());
        TipoAcesso updated = tipoAcessoService.save(toSave);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/tipo-acesso/{id}")
    public ResponseEntity<TipoAcesso> getTipoAcessoById(@PathVariable Long id) {
        return tipoAcessoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
