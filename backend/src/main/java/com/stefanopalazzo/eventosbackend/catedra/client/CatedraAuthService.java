package com.stefanopalazzo.eventosbackend.catedra.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CatedraAuthService {

    private final WebClient.Builder webClientBuilder;

    @Value("${catedra.auth-url}")
    private String authUrl;

    @Value("${catedra.username}")
    private String username;

    @Value("${catedra.password}")
    private String password;

    private String token;

    public String obtenerToken() {
        if (token != null) return token;

        var request = new LoginRequest(username, password);

        AuthResponse response = webClientBuilder.build()
                .post()
                .uri(authUrl)
                .body(Mono.just(request), LoginRequest.class)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .block();

        token = response.token();
        return token;
    }

    record LoginRequest(String username, String password) {}
    record AuthResponse(String token) {}
}
