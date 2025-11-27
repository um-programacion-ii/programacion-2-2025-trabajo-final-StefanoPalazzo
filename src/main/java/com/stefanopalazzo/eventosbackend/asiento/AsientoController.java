package com.stefanopalazzo.eventosbackend.asiento;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
public class AsientoController {

    private final AsientoService asientoService;

    @GetMapping("/{eventoId}")
    public List<Asiento> listar(@PathVariable int eventoId) {
        return asientoService.asientosDelEvento(eventoId);
    }

    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquear(@RequestBody AsientoBloqueoRequest req) {
        try {
            Asiento a = asientoService.bloquear(
                    req.getEventoId(),
                    req.getFila(),
                    req.getColumna()
            );
            return ResponseEntity.ok(a);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/liberar")
    public String liberar(@RequestBody AsientoBloqueoRequest req) {
        asientoService.liberar(req.getEventoId(), req.getFila(), req.getColumna());
        return "OK";
    }

    @GetMapping("/id/{asientoId}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long asientoId) {
        Asiento asiento = asientoService.findById(asientoId);
        if (asiento == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(asiento);
    }
}
