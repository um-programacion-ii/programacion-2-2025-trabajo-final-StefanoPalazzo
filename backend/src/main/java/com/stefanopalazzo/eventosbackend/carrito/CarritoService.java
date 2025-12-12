package com.stefanopalazzo.eventosbackend.carrito;

import com.stefanopalazzo.eventosbackend.asiento.AsientoService;
import com.stefanopalazzo.eventosbackend.evento.Evento;
import com.stefanopalazzo.eventosbackend.evento.EventoRepository;
import com.stefanopalazzo.eventosbackend.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final SessionService sessionService;
    private final EventoRepository eventoRepository;
    private final AsientoService asientoService;

    public List<CarritoItem> getCarrito() {
        return sessionService.getCarrito();
    }

    /** Agregar y BLOQUEAR */
    public void agregarItem(CarritoItem item) {
        // 1. Verificar precio oficial desde Evento
        Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        item.setPrecio(evento.getPrecioEntrada());

        // 2. BLOQUEAR asiento antes de agregarlo
        asientoService.bloquear(item.getEventoId(), item.getFila(), item.getColumna());

        // 3. Agregar al carrito (validación de duplicados está dentro de la sesión)
        List<CarritoItem> carrito = sessionService.getCarrito();
        carrito.add(item);
        sessionService.setCarrito(carrito);
    }

    /** Quitar y LIBERAR asiento */
    public void quitarItem(int eventoId, int fila, int columna) {
        // 1) Liberar asiento en la BD
        asientoService.liberar(eventoId, fila, columna);

        // 2) Quitar de la sesión
        List<CarritoItem> carrito = sessionService.getCarrito();
        carrito.removeIf(item -> item.getEventoId() == eventoId &&
                item.getFila() == fila &&
                item.getColumna() == columna);
        sessionService.setCarrito(carrito);
    }

    /** Limpiar carrito y liberar todo */
    public void limpiar() {
        List<CarritoItem> carrito = sessionService.getCarrito();
        for (CarritoItem item : carrito) {
            asientoService.liberar(item.getEventoId(), item.getFila(), item.getColumna());
        }
        sessionService.limpiarCarrito();
    }
}
