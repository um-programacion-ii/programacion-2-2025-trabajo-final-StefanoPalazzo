package com.stefanopalazzo.eventosbackend.evento;

import com.stefanopalazzo.eventosbackend.catedra.client.CatedraClient;
import com.stefanopalazzo.eventosbackend.catedra.dto.EventoCompletoDto;
import com.stefanopalazzo.eventosbackend.catedra.dto.EventoResumidoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSyncService {

    private final CatedraClient catedraClient;
    private final EventoRepository eventoRepository;

    public int sincronizar() {

        List<EventoResumidoDto> lista;

        try {
            lista = catedraClient.obtenerEventosResumidos();
        } catch (Exception e) {
            System.err.println("No se pudo sincronizar eventos: " + e.getMessage());
            return 0;
        }

        int count = 0;

        for (var resumido : lista) {

            EventoCompletoDto full = catedraClient.obtenerEventoCompleto(resumido.getId());

            Evento evento = Evento.builder()
                    .id(full.getId())
                    .titulo(full.getTitulo())
                    .resumen(full.getResumen())
                    .descripcion(full.getDescripcion())
                    .fecha(full.getFecha())
                    .precioEntrada(full.getPrecioEntrada())
                    .tipo(full.getEventoTipo().getNombre())
                    .direccion(full.getDireccion())
                    .imagen(full.getImagen())
                    .filaAsientos(full.getFilaAsientos())
                    .columnaAsientos(full.getColumnaAsientos())
                    .ultimaActualizacion(LocalDateTime.now().toString())
                    .build();

            eventoRepository.save(evento);
            count++;
        }

        return count;
    }
}
