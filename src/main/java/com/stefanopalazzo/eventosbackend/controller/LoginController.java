package com.stefanopalazzo.eventosbackend.controller;

import com.stefanopalazzo.eventosbackend.security.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {

        // Validación ultra simple por ahora
        if (!request.getPassword().equals("1234")) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(request.getUsername());
        return new TokenResponse(token);
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    static class TokenResponse {
        private final String token;
    }
}
