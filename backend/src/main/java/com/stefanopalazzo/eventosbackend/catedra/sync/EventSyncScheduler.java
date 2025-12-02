package com.stefanopalazzo.eventosbackend.catedra.sync;

import com.stefanopalazzo.eventosbackend.evento.EventSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventSyncScheduler {

    private final EventSyncService syncService;

    @Scheduled(fixedDelay = 120000) // cada 2 minutos
    public void syncPeriodico() {
        try {
            syncService.sincronizar();
        } catch (Exception e) {
            System.out.println("Error en sync automática: " + e.getMessage());
        }
    }
}
