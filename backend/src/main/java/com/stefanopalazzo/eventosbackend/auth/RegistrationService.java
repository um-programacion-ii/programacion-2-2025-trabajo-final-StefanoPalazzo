package com.stefanopalazzo.eventosbackend.auth;

import com.stefanopalazzo.eventosbackend.auth.dto.RegisterRequest;
import com.stefanopalazzo.eventosbackend.auth.dto.RegisterResponse;
import com.stefanopalazzo.eventosbackend.user.User;
import com.stefanopalazzo.eventosbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        // Validate username
        if (userRepository.existsByUsername(request.getUsername())) {
            return new RegisterResponse(false, "El nombre de usuario ya existe", null);
        }

        // Validate email
        if (userRepository.existsByEmail(request.getEmail())) {
            return new RegisterResponse(false, "El email ya está registrado", null);
        }

        // Validate password length
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            return new RegisterResponse(false, "La contraseña debe tener al menos 4 caracteres", null);
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .build();

        userRepository.save(user);

        return new RegisterResponse(true, "Usuario registrado exitosamente", user.getUsername());
    }
}
