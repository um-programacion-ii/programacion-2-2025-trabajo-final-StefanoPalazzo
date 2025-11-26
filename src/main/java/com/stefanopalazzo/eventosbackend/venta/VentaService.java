package com.stefanopalazzo.eventosbackend.venta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanopalazzo.eventosbackend.carrito.Carrito;
import com.stefanopalazzo.eventosbackend.carrito.CarritoItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final Carrito carrito;
    private final ObjectMapper mapper = new ObjectMapper();

    public Venta confirmarVenta(int eventoId) throws JsonProcessingException {

        String json = mapper.writeValueAsString(carrito.getItems());

        Venta venta = Venta.builder()
                .eventoId(eventoId)
                .itemsJson(json)
                .total(carrito.total())
                .fecha(LocalDateTime.now().toString())
                .build();

        ventaRepository.save(venta);
        carrito.limpiar();

        return venta;
    }
    public Object listar() {
        return ventaRepository.findAll();
    }
}
