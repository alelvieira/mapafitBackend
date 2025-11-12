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
import com.mapadavida.mdvBackend.utils.StateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/local")
public class LocalController {

    private static final Logger logger = LoggerFactory.getLogger(LocalController.class);

    @Value("${GOOGLE_API_KEY:${google.api.key:}}")
    private String googleApiKey;

    private final LocalService localService;
    private final EnderecoService enderecoService;

    @Autowired private TipoLocalRepository tipoLocalRepository;
    @Autowired private TipoAtividadeRepository tipoAtividadeRepository;
    @Autowired private TipoAcessoRepository tipoAcessoRepository;

    @Autowired private TipoLocalService tipoLocalService;
    @Autowired private TipoAtividadeService tipoAtividadeService;
    @Autowired private TipoAcessoService tipoAcessoService;

    @Autowired
    public LocalController(LocalService localService, EnderecoService enderecoService) {
        this.localService = localService;
        this.enderecoService = enderecoService;
    }

    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocais() {
        return ResponseEntity.ok(localService.getLocais());
    }

    @GetMapping("/aprovados")
    public List<Local> getAprovados() {
        return localService.listarAprovados();
    }


    // Adiciona endpoints equivalentes com hífen (ex.: /tipo-acesso) para compatibilidade com requests existentes
    @GetMapping("/tipo-local")
    public ResponseEntity<List<TipoLocal>> listarTiposLocaisHyphen() {
        return ResponseEntity.ok(tipoLocalRepository.findAll());
    }

    @GetMapping("/tipo-atividade")
    public ResponseEntity<List<TipoAtividade>> listarTiposAtividadesHyphen() {
        return ResponseEntity.ok(tipoAtividadeRepository.findAll());
    }

    @GetMapping("/tipo-acesso")
    public ResponseEntity<List<TipoAcesso>> listarTiposAcessoHyphen() {
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

        // normalizar e validar estado antes de persistir
        if (localDTO.getEndereco().getEstado() != null) {
            String estadoNorm = StateUtils.normalizeEstado(localDTO.getEndereco().getEstado());
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
            dto.getEndereco().setEstado(com.mapadavida.mdvBackend.utils.StateUtils.normalizeEstado(dto.getEndereco().getEstado()));
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
        // usa a chave injetada das propriedades
        // Primeiro, detectar se o parâmetro é (ou contém) um CEP e, se for, usar ViaCEP

        String normalized = normalizeEndereco(endereco);

        // detectar CEP em vários formatos: 12345678 ou 12345-678
        java.util.regex.Matcher cepMatcher = java.util.regex.Pattern.compile("(\\d{5}-?\\d{3}|\\d{8})").matcher(endereco != null ? endereco : "");
        if (cepMatcher.find()) {
            String digits = cepMatcher.group(1).replaceAll("\\D", "");
            if (digits.length() == 8) {
                String formattedCep = digits.substring(0, 5) + "-" + digits.substring(5);
                String viaCepUrl = "https://viacep.com.br/ws/" + digits + "/json/";
                RestTemplate restTemplate = new RestTemplate();
                try {
                    ResponseEntity<java.util.Map> viaResp = restTemplate.getForEntity(viaCepUrl, java.util.Map.class);
                    if (viaResp.getStatusCode().is2xxSuccessful() && viaResp.getBody() != null) {
                        java.util.Map<?, ?> body = viaResp.getBody();
                        // ViaCEP returns {"erro": true} when not found
                        Object erro = body.get("erro");
                        if (erro instanceof Boolean && ((Boolean) erro)) {
                            return ResponseEntity.status(404).body(Map.of("error", "CEP não encontrado", "postal_code", formattedCep));
                        }

                        String street = body.getOrDefault("logradouro", null) instanceof String ? (String) body.get("logradouro") : null;
                        String neighborhood = body.getOrDefault("bairro", null) instanceof String ? (String) body.get("bairro") : null;
                        String city = body.getOrDefault("localidade", null) instanceof String ? (String) body.get("localidade") : null;
                        String state = body.getOrDefault("uf", null) instanceof String ? (String) body.get("uf") : null;

                        StringBuilder formatted = new StringBuilder();
                        if (street != null && !street.isBlank()) formatted.append(street);
                        if (neighborhood != null && !neighborhood.isBlank()) {
                            if (formatted.length() > 0) formatted.append(", ");
                            formatted.append(neighborhood);
                        }
                        if (city != null && !city.isBlank()) {
                            if (formatted.length() > 0) formatted.append(" - ");
                            formatted.append(city);
                        }
                        if (state != null && !state.isBlank()) formatted.append("/" + state);
                        if (formatted.length() == 0) formatted.append("CEP ").append(formattedCep);

                        java.util.Map<String, Object> out = new java.util.HashMap<>();
                        out.put("latitude", null); // ViaCEP não fornece coordenadas por padrão
                        out.put("longitude", null);
                        out.put("formatted_address", formatted.toString());
                        out.put("street", street);
                        out.put("number", null);
                        out.put("city", city);
                        out.put("state", state);
                        out.put("postal_code", formattedCep);
                        out.put("source", "viacep");
                        out.put("coordinates_source", null);

                        // Se há chave do Google, tentar obter lat/lng pelo endereço formatado (fallback)
                        if (googleApiKey != null && !googleApiKey.isBlank()) {
                            String googleUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="
                                    + URLEncoder.encode(formatted.toString(), StandardCharsets.UTF_8)
                                    + "&key=" + googleApiKey;
                            try {
                                RestTemplate googleClient = new RestTemplate();
                                ResponseEntity<java.util.Map> gResp = googleClient.getForEntity(googleUrl, java.util.Map.class);
                                if (gResp.getStatusCode().is2xxSuccessful() && gResp.getBody() != null) {
                                    Object gStatusObj = gResp.getBody().get("status");
                                    String gStatus = gStatusObj instanceof String ? (String) gStatusObj : null;
                                    if ("OK".equals(gStatus)) {
                                        Object gResultsObj = gResp.getBody().get("results");
                                        if (gResultsObj instanceof java.util.List) {
                                            java.util.List<?> gResults = (java.util.List<?>) gResultsObj;
                                            if (!gResults.isEmpty() && gResults.get(0) instanceof java.util.Map) {
                                                java.util.Map<?, ?> firstG = (java.util.Map<?, ?>) gResults.get(0);
                                                Object geometryObjG = firstG.get("geometry");
                                                if (geometryObjG instanceof java.util.Map) {
                                                    java.util.Map<?, ?> geometryG = (java.util.Map<?, ?>) geometryObjG;
                                                    Object locationObjG = geometryG.get("location");
                                                    if (locationObjG instanceof java.util.Map) {
                                                        java.util.Map<?, ?> locationG = (java.util.Map<?, ?>) locationObjG;
                                                        Object latObj = locationG.get("lat");
                                                        Object lngObj = locationG.get("lng");
                                                        if (latObj instanceof Number && lngObj instanceof Number) {
                                                            double lat = ((Number) latObj).doubleValue();
                                                            double lng = ((Number) lngObj).doubleValue();
                                                            out.put("latitude", lat);
                                                            out.put("longitude", lng);
                                                            out.put("coordinates_source", "google");

                                                            // atualizar formatted_address com o do Google, se disponível
                                                            Object gFormatted = firstG.get("formatted_address");
                                                            if (gFormatted instanceof String) out.put("formatted_address", (String) gFormatted);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // não OK: incluir status do Google para diagnóstico (não falha o retorno via ViaCEP)
                                        out.put("google_status", gStatus);
                                        out.put("google_message", gResp.getBody().get("error_message"));
                                    }
                                } else {
                                    out.put("google_status", gResp.getStatusCodeValue());
                                }
                            } catch (Exception ex) {
                                logger.warn("Falha ao consultar Google Geocoding como fallback para '{}': {}", formatted.toString(), ex.getMessage());
                                out.put("google_error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
                            }
                        }

                        return ResponseEntity.ok(out);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(Map.of("error", "Falha ao consultar ViaCEP", "status", viaResp.getStatusCodeValue()));
                    }
                } catch (Exception ex) {
                    logger.error("Erro ao consultar ViaCEP para cep='{}'. URL='{}'.", endereco, viaCepUrl, ex);
                    String details = ex.getMessage() != null ? ex.getMessage() : ex.toString();
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(Map.of("error", "Falha ao consultar ViaCEP", "details", details));
                }
            }
        }

        // Se não for CEP, usa a chave injetada das propriedades para o Google Geocoding como antes
        if (googleApiKey == null || googleApiKey.isBlank()) {
            logger.error("Chave Google API não configurada (google.api.key)");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Google API key não configurada"));
        }

        // normaliza endereço/CEP antes de codificar e enviar para a API
        // (já temos `normalized` acima)

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + URLEncoder.encode(normalized, StandardCharsets.UTF_8)
                + "&key=" + googleApiKey;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response;

        try {
            response = restTemplate.getForEntity(url, Map.class);

            Map<String, Object> body = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && body != null) {
                // inspeciona o status retornado pela API do Google para ajudar no diagnóstico
                Object statusObj = body.get("status");
                String statusStr = statusObj instanceof String ? (String) statusObj : null;
                if (statusStr != null && !"OK".equals(statusStr)) {
                    logger.warn("Geocoding API retornou status='{}' para URL={}", statusStr, url);
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(Map.of(
                                    "error", "Geocoding API returned non-OK status",
                                    "status", statusStr,
                                    "google_message", body.get("error_message")
                            ));
                }

                Object resultsObj = body.get("results");
                if (resultsObj instanceof java.util.List) {
                    java.util.List<?> results = (java.util.List<?>) resultsObj;
                    if (!results.isEmpty() && results.get(0) instanceof java.util.Map) {
                        java.util.Map<?, ?> first = (java.util.Map<?, ?>) results.get(0);
                        Object geometryObj = first.get("geometry");
                        if (geometryObj instanceof java.util.Map) {
                            java.util.Map<?, ?> geometry = (java.util.Map<?, ?>) geometryObj;
                            Object locationObj = geometry.get("location");
                            if (locationObj instanceof java.util.Map) {
                                java.util.Map<?, ?> location = (java.util.Map<?, ?>) locationObj;
                                Object latObj = location.get("lat");
                                Object lngObj = location.get("lng");
                                if (latObj instanceof Number && lngObj instanceof Number) {
                                    double lat = ((Number) latObj).doubleValue();
                                    double lng = ((Number) lngObj).doubleValue();

                                    // extrair formatted_address
                                    String formatted = null;
                                    Object formattedObj = first.get("formatted_address");
                                    if (formattedObj instanceof String) formatted = (String) formattedObj;

                                    // extrair componentes de endereço (rua, número, cidade, estado, CEP)
                                    String street = null, number = null, city = null, state = null, postal_code = null;
                                    Object componentsObj = first.get("address_components");
                                    if (componentsObj instanceof java.util.List) {
                                        for (Object compObj : (java.util.List<?>) componentsObj) {
                                            if (!(compObj instanceof java.util.Map)) continue;
                                            java.util.Map<?, ?> comp = (java.util.Map<?, ?>) compObj;
                                            Object longNameObj = comp.get("long_name");
                                            String longName = longNameObj instanceof String ? (String) longNameObj : null;
                                            Object typesObj = comp.get("types");
                                            if (!(typesObj instanceof java.util.List)) continue;
                                            for (Object t : (java.util.List<?>) typesObj) {
                                                if (!(t instanceof String)) continue;
                                                String type = (String) t;
                                                switch (type) {
                                                    case "route": if (street == null) street = longName; break;
                                                    case "street_number": if (number == null) number = longName; break;
                                                    case "locality": if (city == null) city = longName; break;
                                                    case "postal_town": if (city == null) city = longName; break;
                                                    case "administrative_area_level_1": if (state == null) state = longName; break;
                                                    case "postal_code": if (postal_code == null) postal_code = longName; break;
                                                }
                                            }
                                        }
                                    }

                                    java.util.Map<String, Object> out = new java.util.HashMap<>();
                                    out.put("latitude", lat);
                                    out.put("longitude", lng);
                                    out.put("formatted_address", formatted);
                                    out.put("street", street);
                                    out.put("number", number);
                                    out.put("city", city);
                                    out.put("state", state);
                                    out.put("postal_code", postal_code);

                                    return ResponseEntity.ok(out);
                                }
                            }
                        }
                    }
                }
            }

            // se chegou aqui, não houve resultados úteis — inclui o status do Google (se presente) para diagnóstico
            return ResponseEntity.status(404).body(Map.of(
                    "error", "Endereço não encontrado",
                    "google_status", response != null && response.getBody() != null ? response.getBody().get("status") : null
            ));
        } catch (Exception ex) {
            logger.error("Erro ao consultar Geocoding API para endereco='{}'. URL='{}'.", endereco, url, ex);
            String details = ex.getMessage() != null ? ex.getMessage() : ex.toString();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Falha ao consultar serviço de geocoding", "details", details));
        }
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

    // método auxiliar para normalizar CEP/rua antes do geocoding
    private String normalizeEndereco(String endereco) {
        if (endereco == null) return "";
        // trim e compressão de espaços múltiplos
        String s = endereco.trim().replaceAll("\\s+", " ");
        // remover acentuação
        s = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // substituir vírgulas/; por vírgula e espaço consistente
        s = s.replaceAll("[;,]+", ", ");

        // detectar CEPs em formatos comuns e normalizar para 5-3 (ex: 81320-490)
        java.util.regex.Pattern pDot = java.util.regex.Pattern.compile("\\b(\\d{2}\\.\\d{3}-\\d{3})\\b");
        java.util.regex.Pattern pHyphen = java.util.regex.Pattern.compile("\\b(\\d{5}-\\d{3})\\b");
        java.util.regex.Pattern pPlain = java.util.regex.Pattern.compile("\\b(\\d{8})\\b");

        java.util.regex.Matcher m = pDot.matcher(s);
        String digits = null;
        if (m.find()) {
            digits = m.group(1).replaceAll("\\D", "");
        } else if ((m = pHyphen.matcher(s)).find()) {
            digits = m.group(1).replaceAll("\\D", "");
        } else if ((m = pPlain.matcher(s)).find()) {
            digits = m.group(1);
        }

        if (digits != null && digits.length() == 8) {
            String formattedCep = digits.substring(0, 5) + "-" + digits.substring(5);
            return "CEP " + formattedCep + ", Brasil"; // ajuda o geocoding a priorizar resultado por CEP
        }

        // para ruas, remover caracteres não-ASCII remanescentes e garantir ordem legível
        s = s.replaceAll("[^\\x00-\\x7F]", ""); // removes non-ASCII characters
        s = s.replaceAll("\\s*,\\s*", ", ");

        return s;
    }

}
