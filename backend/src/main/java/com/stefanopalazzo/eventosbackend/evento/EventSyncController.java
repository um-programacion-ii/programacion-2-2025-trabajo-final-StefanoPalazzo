package com.stefanopalazzo.eventosbackend.evento;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class EventSyncController {

    private final EventSyncService syncService;

    // Recibe un evento enviado por el proxy
    @PostMapping("/evento")
    public String syncEvento(@RequestBody Object data) {
        syncService.sincronizarEventoIndividual(data);
        return "Evento sincronizado correctamente";
    }

    // Mantengo el endpoint viejo por si querés forzar sync completa
    @PostMapping
    public String sync() {
        int count = syncService.sincronizar();
        return "Sincronizados: " + count;
    }
}
