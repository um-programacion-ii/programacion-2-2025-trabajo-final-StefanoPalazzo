package com.stefanopalazzo.eventosbackend.catedra.client;

import com.stefanopalazzo.eventosbackend.catedra.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatedraClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${catedra.base-url}")
    private String baseUrl;

    @Value("${catedra.auth-url}")
    private String authUrl;

    @Value("${catedra.username}")
    private String username;

    @Value("${catedra.password}")
    private String password;

    private String token;

    private WebClient client() {
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + getToken())
                .build();
    }

    private String getToken() {
        if (token != null) return token;

        Map<String, String> body = Map.of(
                "username", username,
                "password", password
        );

        var response = webClientBuilder.build()
                .post()
                .uri(authUrl)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        token = (String) response.get("id_token");
        return token;
    }

    public List<EventoResumidoDto> obtenerEventosResumidos() {
        return client()
                .get()
                .uri("/eventos-resumidos")
                .retrieve()
                .bodyToFlux(EventoResumidoDto.class)
                .collectList()
                .block();
    }

    public EventoCompletoDto obtenerEventoCompleto(int id) {
        return client()
                .get()
                .uri("/evento/{id}", id)
                .retrieve()
                .bodyToMono(EventoCompletoDto.class)
                .block();
    }

    public BloquearAsientosResponseDto bloquearAsientos(BloquearAsientosRequestDto request) {
        return client()
                .post()
                .uri("/bloquear-asientos")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BloquearAsientosResponseDto.class)
                .block();
    }

    public VentaResponseDto realizarVenta(VentaRequestDto request) {
        return client()
                .post()
                .uri("/realizar-venta")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(VentaResponseDto.class)
                .block();
    }
}
