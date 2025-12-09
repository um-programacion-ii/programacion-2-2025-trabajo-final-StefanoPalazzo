package com.stefanopalazzo.proxy.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private String cachedToken;

    private String login() {
        if (cachedToken != null) return cachedToken;

        String url = "http://backend:8081/api/login";
        Map<String, String> req = Map.of("username", "stefano", "password", "1234");

        log.info("➡ Haciendo login contra {}", url);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, req, Map.class);
        cachedToken = response.getBody().get("token").toString();

        log.info("✔ TOKEN obtenido");
        return cachedToken;
    }

    private ResponseEntity<?> proxyGet(String backendUrl) {
        String token = login();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<byte[]> backendResponse =
            restTemplate.exchange(backendUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

        // Construir headers seguros
        HttpHeaders clean = new HttpHeaders();
        MediaType ct = backendResponse.getHeaders().getContentType();

        if (ct == null) {
            clean.setContentType(MediaType.APPLICATION_JSON);
        } else {
            clean.setContentType(ct);
        }

        return new ResponseEntity<>(backendResponse.getBody(), clean, backendResponse.getStatusCode());
    }

    @GetMapping("/eventos-locales")
    public ResponseEntity<?> eventosLocales() {
        log.info("➡ /proxy/eventos-locales");
        return proxyGet("http://backend:8081/api/catedra/eventos");
    }

    @GetMapping("/eventos-remotos")
    public ResponseEntity<?> eventosRemotos() {
        log.info("➡ /proxy/eventos-remotos");
        return proxyGet("http://backend:8081/api/catedra-sync/eventos");
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
