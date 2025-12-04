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
        try {
            if (cachedToken != null) {
                return cachedToken;
            }

            String url = "http://localhost:8081/api/login";

            Map<String, String> req = Map.of(
                "username", "stefano",
                "password", "1234"
            );

            log.info("➡ Haciendo login contra {}", url);

            ResponseEntity<Map> response =
                restTemplate.postForEntity(url, req, Map.class);

            cachedToken = response.getBody().get("token").toString();

            log.info("✔ TOKEN obtenido");

            return cachedToken;

        } catch (Exception e) {
            log.error("❌ ERROR al hacer login: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/eventos-locales")
    public ResponseEntity<String> eventosLocales() {
        log.info("➡ /proxy/eventos-locales");

        String token = login();
        String url = "http://localhost:8081/api/catedra/eventos";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        return ResponseEntity.ok(response.getBody());
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("PING OK");
        return ResponseEntity.ok("pong");
    }



    @GetMapping("/eventos-remotos")
    public ResponseEntity<String> eventosRemotos() {
        log.info("➡ /proxy/eventos-remotos");

        String token = login();
        String url = "http://localhost:8081/api/catedra-sync/eventos";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        return ResponseEntity.ok(response.getBody());
    }
}
