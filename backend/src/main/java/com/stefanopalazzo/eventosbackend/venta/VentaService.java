package com.stefanopalazzo.eventosbackend.venta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanopalazzo.eventosbackend.asiento.Asiento;
import com.stefanopalazzo.eventosbackend.asiento.AsientoService;
import com.stefanopalazzo.eventosbackend.carrito.Carrito;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final Carrito carrito;
    private final AsientoService asientoService;
    private final ObjectMapper mapper = new ObjectMapper();

    public Venta confirmarVenta(int eventoId) throws JsonProcessingException {

        // 1) vender asientos → bloquear definitivamente
        List<Asiento> vendidos = asientoService.venderAsientos(carrito.getItems());

        // 2) serializar items
        String json = mapper.writeValueAsString(vendidos);

        // 3) guardar venta
        Venta venta = Venta.builder()
                .eventoId(eventoId)
                .itemsJson(json)
                .total(carrito.total())
                .fecha(LocalDateTime.now().toString())
                .build();

        ventaRepository.save(venta);

        // 4) limpiar carrito
        carrito.limpiar();

        return venta;
    }

    public Object listar() {
        return ventaRepository.findAll();
    }
}
