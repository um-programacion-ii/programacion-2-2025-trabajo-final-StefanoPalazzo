package com.stefanopalazzo.eventosbackend.auth;

import com.stefanopalazzo.eventosbackend.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        try {
            sessionService.invalidarSesion();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sesión cerrada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cerrar sesión");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/session/status")
    public ResponseEntity<Map<String, Object>> sessionStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("eventoSeleccionado", sessionService.getEventoSeleccionado());
            status.put("pasoFlujo", sessionService.getPasoFlujo());
            status.put("carritoSize", sessionService.getCarrito().size());
            status.put("active", true);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            Map<String, Object> status = new HashMap<>();
            status.put("active", false);
            status.put("error", e.getMessage());
            return ResponseEntity.ok(status);
        }
    }
}
