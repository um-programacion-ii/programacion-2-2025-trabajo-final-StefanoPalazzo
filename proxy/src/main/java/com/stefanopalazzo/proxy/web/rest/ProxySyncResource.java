package com.stefanopalazzo.proxy.web.rest;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/proxy")
public class ProxySyncResource {

    private static final Logger log = LoggerFactory.getLogger(ProxySyncResource.class);

    @Value("${proxy.chair-url}")
    private String chairUrl;

    @Value("${proxy.token}")
    private String token;

    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;

    public ProxySyncResource(RestTemplate restTemplate, StringRedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        log.info("ProxySyncResource initialized!");
    }

    @GetMapping("/test")
    public String test() {
        return "proxy OK";
    }

    @GetMapping("/asientos/{id}")
    public String getAsientos(@PathVariable Long id) {
        String key = "evento_" + id;
        String json = redisTemplate.opsForValue().get(key);
        log.info("Proxy -> Consultando Redis clave: {} -> Resultado: {}", key, json != null ? "ENCONTRADO" : "NULL");
        return json != null ? json : "{}";
    }

    private HttpEntity<String> buildEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, headers);
    }

    @PostMapping("/bloquear-asientos")
    public String bloquear(@RequestBody String body) {
        String url = chairUrl + "/endpoints/v1/bloquear-asientos";
        log.info("Proxy -> Forwarding to Chair: {}", url);
        return restTemplate.postForObject(url, buildEntity(body), String.class);
    }

    @PostMapping("/realizar-venta")
    public String realizarVenta(@RequestBody String body) {
        String url = chairUrl + "/endpoints/v1/realizar-venta";
        log.info("Proxy -> Forwarding to Chair: {}", url);
        return restTemplate.postForObject(url, buildEntity(body), String.class);
    }

    @GetMapping("/listar-ventas")
    public String listarVentas() {
        String url = chairUrl + "/endpoints/v1/listar-ventas";
        log.info("Proxy -> Forwarding to Chair: {}", url);
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, buildEntity(null), String.class)
                .getBody();
    }

    @GetMapping("/listar-venta/{id}")
    public String listarVenta(@PathVariable Long id) {
        String url = chairUrl + "/endpoints/v1/listar-venta/" + id;
        log.info("Proxy -> Forwarding to Chair: {}", url);
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, buildEntity(null), String.class)
                .getBody();
    }
}
