package com.stefanopalazzo.eventosbackend.auth;

import com.stefanopalazzo.eventosbackend.auth.dto.RegisterRequest;
import com.stefanopalazzo.eventosbackend.auth.dto.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = registrationService.register(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
