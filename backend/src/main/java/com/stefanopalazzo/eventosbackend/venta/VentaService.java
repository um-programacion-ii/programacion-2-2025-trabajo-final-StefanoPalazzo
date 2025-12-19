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

import com.stefanopalazzo.eventosbackend.user.User;
import com.stefanopalazzo.eventosbackend.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final Carrito carrito;
    private final AsientoService asientoService;
    private final UserRepository userRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public Venta confirmarVenta(int eventoId) throws JsonProcessingException {

        // 0) Obtener usuario actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

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
                .userId(user.getId())
                .build();

        ventaRepository.save(venta);

        // 4) limpiar carrito
        carrito.limpiar();

        return venta;
    }

    public RealizarVentaResponse realizarVentaMobile(RealizarVentaDto request) throws JsonProcessingException {
        // 0) Obtener usuario actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // 1) Convertir los asientos del DTO a entidades Asiento
        List<Asiento> asientos = request.getAsientos().stream().map(dto -> Asiento.builder()
                .eventoId(request.getEventoId().intValue())
                .fila(dto.getFila())
                .columna(dto.getColumna())
                .estado(com.stefanopalazzo.eventosbackend.asiento.AsientoEstado.VENDIDO)
                .precio(request.getPrecioVenta() / request.getAsientos().size())
                .build()).collect(java.util.stream.Collectors.toList());

        // 2) Serializar asientos
        String json = mapper.writeValueAsString(asientos);

        // 3) Guardar venta
        Venta venta = Venta.builder()
                .eventoId(request.getEventoId().intValue())
                .itemsJson(json)
                .total(request.getPrecioVenta())
                .fecha(request.getFecha())
                .userId(user.getId())
                .build();

        ventaRepository.save(venta);

        // 4) Retornar respuesta
        return RealizarVentaResponse.builder()
                .ventaId((long) venta.getId())
                .eventoId(request.getEventoId())
                .fechaVenta(request.getFecha())
                .precioVenta(request.getPrecioVenta())
                .resultado(true)
                .descripcion("Venta confirmada")
                .asientos(asientos)
                .build();
    }

    public List<RealizarVentaResponse> listar() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        List<Venta> ventas = ventaRepository.findByUserId(user.getId());

        return ventas.stream().map(venta -> {
            try {
                List<Asiento> asientos = mapper.readValue(
                        venta.getItemsJson(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Asiento.class));

                return RealizarVentaResponse.builder()
                        .ventaId((long) venta.getId())
                        .eventoId((long) venta.getEventoId())
                        .fechaVenta(venta.getFecha())
                        .precioVenta(venta.getTotal())
                        .resultado(true)
                        .descripcion("Venta confirmada")
                        .asientos(asientos)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar venta " + venta.getId(), e);
            }
        }).collect(java.util.stream.Collectors.toList());
    }
}
