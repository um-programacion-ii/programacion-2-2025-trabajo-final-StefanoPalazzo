package com.stefanopalazzo.eventosbackend.catedra.controller;

import com.stefanopalazzo.eventosbackend.catedra.client.CatedraClient;
import com.stefanopalazzo.eventosbackend.catedra.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catedra")
@RequiredArgsConstructor
public class CatedraController {

    private final CatedraClient catedraClient;

    @GetMapping("/eventos")
    public List<EventoResumidoDto> eventos() {
        return catedraClient.obtenerEventosResumidos();
    }

    @GetMapping("/eventos/{id}")
    public EventoCompletoDto eventoPorId(@PathVariable int id) {
        return catedraClient.obtenerEventoCompleto(id);
    }

    @PostMapping("/bloquear-asientos")
    public BloquearAsientosResponseDto bloquear(@RequestBody BloquearAsientosRequestDto request) {
        return catedraClient.bloquearAsientos(request);
    }

    @PostMapping("/realizar-venta")
    public VentaResponseDto venta(@RequestBody VentaRequestDto request) {
        return catedraClient.realizarVenta(request);
    }
}

