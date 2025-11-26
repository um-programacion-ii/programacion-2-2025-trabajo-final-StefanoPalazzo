package com.stefanopalazzo.eventosbackend.carrito;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class Carrito {

    private final List<CarritoItem> items = new ArrayList<>();

    public void agregar(CarritoItem item) {
        items.add(item);
    }

    public void quitar(CarritoItem item) {
        items.removeIf(i ->
                i.getEventoId() == item.getEventoId() &&
                        i.getFila() == item.getFila() &&
                        i.getColumna() == item.getColumna());
    }

    public void limpiar() {
        items.clear();
    }

    public double total() {
        return items.stream().mapToDouble(CarritoItem::getPrecio).sum();
    }
}
