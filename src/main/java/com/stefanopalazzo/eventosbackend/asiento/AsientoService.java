package com.stefanopalazzo.eventosbackend.asiento;

import com.stefanopalazzo.eventosbackend.carrito.Carrito;
import com.stefanopalazzo.eventosbackend.carrito.CarritoItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsientoService {

    private final AsientoRepository asientoRepository;
    private final Carrito carrito;

    @PostConstruct
    public void generarAsientos() {
        generar(1, 10, 12, 2577.20);
        generar(2, 12, 14, 4514.52);
    }

    private void generar(int eventoId, int filas, int columnas, double precio) {
        if (asientoRepository.findByEventoId(eventoId).isEmpty()) {
            for (int f = 1; f <= filas; f++) {
                for (int c = 1; c <= columnas; c++) {
                    asientoRepository.save(
                            Asiento.builder()
                                    .eventoId(eventoId)
                                    .fila(f)
                                    .columna(c)
                                    .estado(AsientoEstado.LIBRE)
                                    .precio(precio)
                                    .build()
                    );
                }
            }
        }
    }

    public List<Asiento> asientosDelEvento(int eventoId) {
        return asientoRepository.findByEventoId(eventoId);
    }

    public Asiento findById(Long id) {
        return asientoRepository.findById(id).orElse(null);
    }

    public Asiento bloquear(int eventoId, int fila, int columna) {
        Asiento asiento = asientoRepository.findByEventoIdAndFilaAndColumna(eventoId, fila, columna)
                .orElseThrow(() -> new RuntimeException("No existe ese asiento"));

        if (asiento.getEstado() != AsientoEstado.LIBRE)
            throw new RuntimeException("Asiento no disponible");

        asiento.setEstado(AsientoEstado.BLOQUEADO);
        return asientoRepository.save(asiento);
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

        List<Asiento> vendidos = new ArrayList<>();

        for (CarritoItem item : items) {

            Asiento asiento = asientoRepository
                    .findByEventoIdAndFilaAndColumna(item.getEventoId(), item.getFila(), item.getColumna())
                    .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

            if (asiento.getEstado() != AsientoEstado.BLOQUEADO) {
                throw new RuntimeException("El asiento ya no está disponible");
            }

            asiento.setEstado(AsientoEstado.VENDIDO);
            asientoRepository.save(asiento);

            vendidos.add(asiento);
        }

        return vendidos;
    }
}
