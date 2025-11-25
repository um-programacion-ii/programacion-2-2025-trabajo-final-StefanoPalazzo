package com.stefanopalazzo.eventosbackend.catedra.controller;

import com.stefanopalazzo.eventosbackend.catedra.client.CatedraClient;
import com.stefanopalazzo.eventosbackend.evento.EventSyncService;
import com.stefanopalazzo.eventosbackend.evento.Evento;
import com.stefanopalazzo.eventosbackend.evento.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catedra-sync")
@RequiredArgsConstructor
public class CatedraSyncController {

    private final EventSyncService syncService;
    private final EventoRepository eventoRepository;

    @PostMapping("/sincronizar")
    public String sincronizar() {
        int count = syncService.sincronizar();
        return "Sincronización completada: " + count;
    }

    @GetMapping("/eventos")
    public List<Evento> listarEventosLocales() {
        return eventoRepository.findAll();
    }

    @GetMapping("/eventos/{id}")
    public Evento obtenerEventoLocal(@PathVariable int id) {
        return eventoRepository.findById(id).orElse(null);
    }
}
