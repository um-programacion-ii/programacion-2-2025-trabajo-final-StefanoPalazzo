package com.stefanopalazzo.eventosbackend.carrito;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class Carrito {

    private final List<CarritoItem> items = new ArrayList<>();

    /** Agregar validando duplicados */
    public void agregar(CarritoItem item) {

        boolean yaExiste = items.stream().anyMatch(i ->
                i.getEventoId() == item.getEventoId() &&
                        i.getFila() == item.getFila() &&
                        i.getColumna() == item.getColumna()
        );

        if (yaExiste) {
            throw new RuntimeException("El asiento ya está en el carrito");
        }

        items.add(item);
    }


    /** Quitar item */
    public void quitar(CarritoItem item) {
        items.removeIf(i ->
                i.getEventoId() == item.getEventoId() &&
                        i.getFila() == item.getFila() &&
                        i.getColumna() == item.getColumna()
        );
    }

    /** Vaciar carrito */
    public void limpiar() {
        items.clear();
    }

    /** Total */
    public double total() {
        return items.stream().mapToDouble(CarritoItem::getPrecio).sum();
    }

    public void quitar(int eventoId, int fila, int columna) {
        items.removeIf(i ->
                i.getEventoId() == eventoId &&
                        i.getFila() == fila &&
                        i.getColumna() == columna
        );
    }

}
