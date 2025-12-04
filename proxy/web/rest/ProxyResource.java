package com.stefanopalazzo.proxy.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/proxy")
public class ProxyResource {

    private final WebClient webClient = WebClient.create();

    @GetMapping("/eventos/{id}")
    public ResponseEntity<?> getEventoProxy(@PathVariable Integer id) {
        var result = webClient.get()
            .uri("http://localhost:8081/api/eventos/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return ResponseEntity.ok(result);
    }
}
