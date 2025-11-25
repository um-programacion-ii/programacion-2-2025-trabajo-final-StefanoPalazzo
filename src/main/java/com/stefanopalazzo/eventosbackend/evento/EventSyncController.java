package com.stefanopalazzo.eventosbackend.evento;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class EventSyncController {

    private final EventSyncService syncService;

    @PostMapping
    public String sync() {
        int count = syncService.sincronizar();
        return "Sincronizados: " + count;
    }
}
