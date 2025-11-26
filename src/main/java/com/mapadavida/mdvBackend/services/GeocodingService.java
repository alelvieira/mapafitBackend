package com.mapadavida.mdvBackend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GeocodingService {
    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    @Value("${GOOGLE_API_KEY:${google.api.key:}}")
    private String googleApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> geocode(String endereco) {
        String normalized = normalizeEndereco(endereco);

        // detectar CEP em vários formatos: 12345678 ou 12345-678
        java.util.regex.Matcher cepMatcher = java.util.regex.Pattern.compile("(\\d{5}-?\\d{3}|\\d{8})").matcher(endereco != null ? endereco : "");
        if (cepMatcher.find()) {
            String digits = cepMatcher.group(1).replaceAll("\\D", "");
            if (digits.length() == 8) {
                String formattedCep = digits.substring(0, 5) + "-" + digits.substring(5);
                return geocodeViaCep(formattedCep, digits, endereco);
            }
        }

        // Se não for CEP, usa a chave para o Google Geocoding
        if (googleApiKey == null || googleApiKey.isBlank()) {
            logger.error("Chave Google API não configurada (google.api.key)");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Google API key não configurada"));
        }

        return geocodeUsingGoogle(normalized);
    }

    private ResponseEntity<?> geocodeViaCep(String formattedCep, String digits, String originalEndereco) {
        String viaCepUrl = "https://viacep.com.br/ws/" + digits + "/json/";
        try {
            ResponseEntity<java.util.Map> viaResp = restTemplate.getForEntity(viaCepUrl, java.util.Map.class);
            if (viaResp.getStatusCode().is2xxSuccessful() && viaResp.getBody() != null) {
                java.util.Map<?, ?> body = viaResp.getBody();
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
                    if (!formatted.isEmpty()) formatted.append(", ");
                    formatted.append(neighborhood);
                }
                if (city != null && !city.isBlank()) {
                    if (!formatted.isEmpty()) formatted.append(" - ");
                    formatted.append(city);
                }
                if (state != null && !state.isBlank()) formatted.append('/').append(state);
                if (formatted.isEmpty()) formatted.append("CEP ").append(formattedCep);

                java.util.Map<String, Object> out = new java.util.HashMap<>();
                out.put("latitude", null);
                out.put("longitude", null);
                out.put("formatted_address", formatted.toString());
                out.put("street", street);
                out.put("number", null);
                out.put("city", city);
                out.put("state", state);
                out.put("postal_code", formattedCep);
                out.put("source", "viacep");
                out.put("coordinates_source", null);

                // fallback para Google se houver chave
                if (googleApiKey != null && !googleApiKey.isBlank()) {
                    String googleUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="
                            + URLEncoder.encode(formatted.toString(), StandardCharsets.UTF_8)
                            + "&key=" + googleApiKey;
                    try {
                        ResponseEntity<java.util.Map> gResp = restTemplate.getForEntity(googleUrl, java.util.Map.class);
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

                                                    Object gFormatted = firstG.get("formatted_address");
                                                    if (gFormatted instanceof String) out.put("formatted_address", (String) gFormatted);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                out.put("google_status", gStatus);
                                out.put("google_message", gResp.getBody().get("error_message"));
                            }
                        } else {
                            out.put("google_status", gResp.getStatusCode().value());
                        }
                    } catch (Exception ex) {
                        logger.warn("Falha ao consultar Google Geocoding como fallback para '{}': {}", formatted.toString(), ex.getMessage());
                        out.put("google_error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
                    }
                }

                return ResponseEntity.ok(out);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Map.of("error", "Falha ao consultar ViaCEP", "status", viaResp.getStatusCode().value()));
            }
        } catch (Exception ex) {
            logger.error("Erro ao consultar ViaCEP para cep='{}'. URL='{}'.", originalEndereco, viaCepUrl, ex);
            String details = ex.getMessage() != null ? ex.getMessage() : ex.toString();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Falha ao consultar ViaCEP", "details", details));
        }
    }

    private ResponseEntity<?> geocodeUsingGoogle(String normalized) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + URLEncoder.encode(normalized, StandardCharsets.UTF_8)
                + "&key=" + googleApiKey;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            Map<String, Object> body = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && body != null) {
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

                                    String formatted = null;
                                    Object formattedObj = first.get("formatted_address");
                                    if (formattedObj instanceof String) formatted = (String) formattedObj;

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

            return ResponseEntity.status(404).body(Map.of(
                    "error", "Endereço não encontrado",
                    "google_status", response != null && response.getBody() != null ? response.getBody().get("status") : null
            ));
        } catch (Exception ex) {
            logger.error("Erro ao consultar Geocoding API para endereco='{}'. URL='{}'.", normalized, url, ex);
            String details = ex.getMessage() != null ? ex.getMessage() : ex.toString();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Falha ao consultar serviço de geocoding", "details", details));
        }
    }

    // util
    private String normalizeEndereco(String endereco) {
        if (endereco == null) return "";
        String s = endereco.trim().replaceAll("\\s+", " ");
        s = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        s = s.replaceAll("[;,]+", ", ");

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
            return "CEP " + formattedCep + ", Brasil";
        }

        s = s.replaceAll("[^\\x00-\\x7F]", "");
        s = s.replaceAll("\\s*,\\s*", ", ");

        return s;
    }
}

