package com.stefanopalazzo.eventosbackend.venta;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping("/confirmar/{eventoId}")
    public Venta confirmar(@PathVariable int eventoId) throws Exception {
        return ventaService.confirmarVenta(eventoId);
    }

    @PostMapping("/realizar")
    public RealizarVentaResponse realizarVenta(@RequestBody RealizarVentaDto request) throws Exception {
        return ventaService.realizarVentaMobile(request);
    }

    @GetMapping
    public java.util.List<RealizarVentaResponse> listar() {
        return ventaService.listar();
    }
}
