package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import com.mapadavida.mdvBackend.models.entities.*;
import com.mapadavida.mdvBackend.repositories.LocalRepository;
import com.mapadavida.mdvBackend.repositories.TipoAcessoRepository;
import com.mapadavida.mdvBackend.repositories.TipoAtividadeRepository;
import com.mapadavida.mdvBackend.repositories.TipoLocalRepository;
import com.mapadavida.mdvBackend.services.EnderecoService;
import com.mapadavida.mdvBackend.services.LocalService;
import com.mapadavida.mdvBackend.services.TipoAcessoService;
import com.mapadavida.mdvBackend.services.TipoAtividadeService;
import com.mapadavida.mdvBackend.services.TipoLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/local")
public class LocalController {

    private final LocalService localService;
    private final EnderecoService enderecoService;
    private final LocalRepository localRepository;

    @Autowired private TipoLocalRepository tipoLocalRepository;
    @Autowired private TipoAtividadeRepository tipoAtividadeRepository;
    @Autowired private TipoAcessoRepository tipoAcessoRepository;

    @Autowired private TipoLocalService tipoLocalService;
    @Autowired private TipoAtividadeService tipoAtividadeService;
    @Autowired private TipoAcessoService tipoAcessoService;

    @Autowired
    public LocalController(LocalService localService, EnderecoService enderecoService, LocalRepository localRepository) {
        this.localService = localService;
        this.enderecoService = enderecoService;
        this.localRepository = localRepository;
    }

    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocais() {
        return ResponseEntity.ok(localService.getLocais());
    }

    @GetMapping("/aprovados")
    public List<Local> getAprovados() {
        return localService.listarAprovados();
    }

    @GetMapping("/tipo_local/{id}")
    public ResponseEntity<List<LocalDTO>> getLocaisByTipo(@PathVariable Long id) {
        return ResponseEntity.ok(localService.findLocaisByTipoLocal(id));
    }

    @GetMapping("/tipo_atividade/{id}")
    public ResponseEntity<List<LocalDTO>> getLocaisByTipoAtividade(@PathVariable Long id) {
        return ResponseEntity.ok(localService.findLocaisByTipoAtividade(id));
    }

    @GetMapping("/tipo_acesso/{id}")
    public ResponseEntity<List<LocalDTO>> getLocaisByTipoAcesso(@PathVariable Long id) {
        return ResponseEntity.ok(localService.findLocaisByTipoAcesso(id));
    }

    @GetMapping("/tipo_local")
    public ResponseEntity<List<TipoLocal>> listarTiposLocais() {
        return ResponseEntity.ok(tipoLocalRepository.findAll());
    }

    @GetMapping("/tipo_atividade")
    public ResponseEntity<List<TipoAtividade>> listarTiposAtividades() {
        return ResponseEntity.ok(tipoAtividadeRepository.findAll());
    }

    @GetMapping("/tipo_acesso")
    public ResponseEntity<List<TipoAcesso>> listarTiposAcesso() {
        return ResponseEntity.ok(tipoAcessoRepository.findAll());
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<LocalDTO>> buscarProximos(@RequestParam double latitude,
                                                         @RequestParam double longitude,
                                                         @RequestParam double raio) {
        return ResponseEntity.ok(localService.findLocaisProximos(latitude, longitude, raio));
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
        return ResponseEntity.ok(localService.updateLocal(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocal(@PathVariable Long id) {
        localService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/geocode")
    public ResponseEntity<?> geocode(@RequestParam String endereco) {
        String apiKey = "AIzaSyCtblRaM76V0Qdyea2f34MPYFmQNbzb9Eo";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + URLEncoder.encode(endereco, StandardCharsets.UTF_8)
                + "&key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List results = (List) response.getBody().get("results");
            if (!results.isEmpty()) {
                Map geometry = (Map) ((Map) results.get(0)).get("geometry");
                Map location = (Map) geometry.get("location");

                double lat = (double) location.get("lat");
                double lng = (double) location.get("lng");

                return ResponseEntity.ok(Map.of("latitude", lat, "longitude", lng));
            }
        }

        return ResponseEntity.status(404).body(Map.of("error", "Endereço não encontrado"));
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

}
