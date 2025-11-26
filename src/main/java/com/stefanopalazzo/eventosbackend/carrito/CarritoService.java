package com.stefanopalazzo.eventosbackend.carrito;

import com.stefanopalazzo.eventosbackend.evento.Evento;
import com.stefanopalazzo.eventosbackend.evento.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final Carrito carrito;
    private final EventoRepository eventoRepository;

    public Carrito getCarrito() {
        return carrito;
    }

    public void agregarItem(CarritoItem item) {

        // buscamos el evento para obtener el precio real
        Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // el precio viene del backend, no del cliente
        item.setPrecio(evento.getPrecioEntrada());

        carrito.agregar(item);
    }

    public void quitarItem(CarritoItem item) {
        carrito.quitar(item);
    }

    public void limpiar() {
        carrito.limpiar();
    }
}
