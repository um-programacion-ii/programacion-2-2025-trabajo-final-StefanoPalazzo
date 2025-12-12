package com.stefanopalazzo.eventosbackend.asiento;

import com.stefanopalazzo.eventosbackend.carrito.Carrito;
import com.stefanopalazzo.eventosbackend.carrito.CarritoItem;
import com.stefanopalazzo.eventosbackend.evento.EventSyncService;
import com.stefanopalazzo.eventosbackend.evento.Evento;
import com.stefanopalazzo.eventosbackend.evento.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AsientoService {

    private final AsientoRepository asientoRepository;

    private final EventSyncService eventSyncService;
    private final EventoRepository eventoRepository;

    public List<Asiento> asientosDelEvento(int eventoId) {
        Evento evento = eventoRepository.findById(eventoId).orElse(null);
        if (evento == null)
            return new ArrayList<>();

        Map<String, String> proxyMap = eventSyncService.getSeatMapFromProxy(eventoId);
        List<Asiento> lista = new ArrayList<>();

        for (int f = 1; f <= evento.getFilaAsientos(); f++) {
            for (int c = 1; c <= evento.getColumnAsientos(); c++) {
                String key = f + "_" + c;
                String estadoStr = proxyMap.getOrDefault(key, "Libre");
                AsientoEstado estado = mapEstado(estadoStr);

                Asiento a = Asiento.builder()
                        .eventoId(eventoId)
                        .fila(f)
                        .columna(c)
                        .estado(estado)
                        .precio(evento.getPrecioEntrada())
                        .build();
                lista.add(a);
            }
        }
        return lista;
    }

    private AsientoEstado mapEstado(String estado) {
        if (estado == null)
            return AsientoEstado.LIBRE;
        // The Chair's Redis returns "Bloqueado", "Vendido" (Capitalized?)
        // Let's be case insensitive
        switch (estado.toLowerCase()) {
            case "bloqueado":
                return AsientoEstado.BLOQUEADO;
            case "vendido":
                return AsientoEstado.VENDIDO;
            default:
                return AsientoEstado.LIBRE;
        }
    }

    public Asiento findById(Long id) {
        return asientoRepository.findById(id).orElse(null);
    }

    public Asiento bloquear(int eventoId, int fila, int columna) {
        List<Map<String, Integer>> asientos = new ArrayList<>();
        Map<String, Integer> asientoMap = Map.of("fila", fila, "columna", columna);
        asientos.add(asientoMap);

        boolean success = eventSyncService.bloquearAsientos(eventoId, asientos);

        if (success) {
            return Asiento.builder()
                    .eventoId(eventoId)
                    .fila(fila)
                    .columna(columna)
                    .estado(AsientoEstado.BLOQUEADO)
                    .build();
        } else {
            throw new RuntimeException("No se pudo bloquear el asiento");
        }
    }

    public void liberar(int eventoId, int fila, int columna) {
        asientoRepository.findByEventoIdAndFilaAndColumna(eventoId, fila, columna)
                .ifPresent(a -> {
                    if (a.getEstado() == AsientoEstado.BLOQUEADO) {
                        a.setEstado(AsientoEstado.LIBRE);
                        asientoRepository.save(a);
                    }
                });
    }

    public List<Asiento> venderAsientos(List<CarritoItem> items) {
        if (items.isEmpty())
            return new ArrayList<>();

        int eventoId = items.get(0).getEventoId();
        List<Map<String, Object>> asientosParaProxy = new ArrayList<>();
        double totalPrecio = 0;

        for (CarritoItem item : items) {
            Map<String, Object> asientoMap = new HashMap<>();
            asientoMap.put("fila", item.getFila());
            asientoMap.put("columna", item.getColumna());
            asientoMap.put("persona", "Usuario Generico"); // TODO: Get real user name
            asientosParaProxy.add(asientoMap);
            // Assuming fixed price or fetching from event, for now placeholder
            totalPrecio += 1000.0;
        }

        boolean success = eventSyncService.realizarVenta(eventoId, totalPrecio, asientosParaProxy);

        if (!success) {
            throw new RuntimeException("La venta fue rechazada por la cátedra");
        }

        List<Asiento> vendidos = new ArrayList<>();
        for (CarritoItem item : items) {
            Asiento asiento = asientoRepository
                    .findByEventoIdAndFilaAndColumna(item.getEventoId(), item.getFila(), item.getColumna())
                    .orElse(Asiento.builder()
                            .eventoId(item.getEventoId())
                            .fila(item.getFila())
                            .columna(item.getColumna())
                            .build());

            asiento.setEstado(AsientoEstado.VENDIDO);
            asientoRepository.save(asiento);
            vendidos.add(asiento);
        }

        return vendidos;
    }
}
