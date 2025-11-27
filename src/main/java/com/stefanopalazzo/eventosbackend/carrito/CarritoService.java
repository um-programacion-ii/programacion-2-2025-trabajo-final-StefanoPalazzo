package com.stefanopalazzo.eventosbackend.carrito;

import com.stefanopalazzo.eventosbackend.asiento.AsientoService;
import com.stefanopalazzo.eventosbackend.evento.Evento;
import com.stefanopalazzo.eventosbackend.evento.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final Carrito carrito;
    private final EventoRepository eventoRepository;
    private final AsientoService asientoService;

    public Carrito getCarrito() {
        return carrito;
    }

    /** Agregar y BLOQUEAR */
    public void agregarItem(CarritoItem item) {

        // 1. Verificar precio oficial desde Evento
        Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        item.setPrecio(evento.getPrecioEntrada());

        // 2. BLOQUEAR asiento antes de agregarlo
        asientoService.bloquear(item.getEventoId(), item.getFila(), item.getColumna());

        // 3. Agregar al carrito (validación de duplicados está dentro del carrito)
        carrito.agregar(item);
    }

    /** Quitar y LIBERAR asiento */
    public void quitarItem(int eventoId, int fila, int columna) {

        // 1) Liberar asiento en la BD
        asientoService.liberar(eventoId, fila, columna);

        // 2) Quitar solo por identificador (sin precio)
        carrito.quitar(eventoId, fila, columna);
    }

    /** Limpiar carrito y liberar todo */
    public void limpiar() {

        for (CarritoItem item : carrito.getItems()) {
            asientoService.liberar(item.getEventoId(), item.getFila(), item.getColumna());
        }

        carrito.limpiar();
    }
}
