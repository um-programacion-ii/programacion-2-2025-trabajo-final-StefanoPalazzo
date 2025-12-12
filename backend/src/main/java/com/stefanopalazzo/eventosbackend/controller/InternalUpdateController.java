package com.stefanopalazzo.eventosbackend.controller;

import com.stefanopalazzo.eventosbackend.evento.EventSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/eventos")
public class InternalUpdateController {

    private static final Logger log = LoggerFactory.getLogger(InternalUpdateController.class);

    private final EventSyncService eventSyncService;

    public InternalUpdateController(EventSyncService eventSyncService) {
        this.eventSyncService = eventSyncService;
    }

    @PostMapping("/actualizacion")
    public void recibirActualizacion(@RequestBody String mensaje) {
        log.info("Backend recibió notificación de actualización: {}", mensaje);
        // Trigger sync or invalidation logic here
        // For now, we just log it, but ideally we should parse the message to know
        // which event changed
        // and fetch the new seat map from Proxy.
        eventSyncService.handleUpdate(mensaje);
    }
}
