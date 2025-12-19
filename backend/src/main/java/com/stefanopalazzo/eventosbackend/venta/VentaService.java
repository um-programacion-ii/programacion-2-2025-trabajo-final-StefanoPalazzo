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
    private final com.stefanopalazzo.eventosbackend.evento.EventSyncService eventSyncService;
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

        // 1) Preparar asientos para el proxy con los datos correctos del mobile
        List<java.util.Map<String, Object>> asientosParaProxy = new java.util.ArrayList<>();
        for (AsientoVentaDto dto : request.getAsientos()) {
            java.util.Map<String, Object> asientoMap = new java.util.HashMap<>();
            asientoMap.put("fila", dto.getFila());
            asientoMap.put("columna", dto.getColumna());
            asientoMap.put("persona", dto.getPersona());
            asientosParaProxy.add(asientoMap);
        }

        // 2) Llamar al proxy/cátedra con los datos reales
        boolean success = eventSyncService.realizarVenta(
                request.getEventoId().intValue(),
                request.getFecha(),
                request.getPrecioVenta(),
                asientosParaProxy);
        if (!success) {
            throw new RuntimeException("La venta fue rechazada por la cátedra");
        }

        // 3) Crear asientos sold para guardar localmente
        List<Asiento> vendidos = request.getAsientos().stream().map(dto -> Asiento.builder()
                .eventoId(request.getEventoId().intValue())
                .fila(dto.getFila())
                .columna(dto.getColumna())
                .estado(com.stefanopalazzo.eventosbackend.asiento.AsientoEstado.VENDIDO)
                .precio(request.getPrecioVenta() / request.getAsientos().size())
                .persona(dto.getPersona())
                .build()).collect(java.util.stream.Collectors.toList());

        // 4) Serializar asientos
        String json = mapper.writeValueAsString(vendidos);

        // 5) Guardar venta
        Venta venta = Venta.builder()
                .eventoId(request.getEventoId().intValue())
                .itemsJson(json)
                .total(request.getPrecioVenta())
                .fecha(request.getFecha())
                .userId(user.getId())
                .build();

        ventaRepository.save(venta);

        // 6) Retornar respuesta
        return RealizarVentaResponse.builder()
                .ventaId((long) venta.getId())
                .eventoId(request.getEventoId())
                .fechaVenta(request.getFecha())
                .precioVenta(request.getPrecioVenta())
                .resultado(true)
                .descripcion("Venta confirmada")
                .asientos(vendidos)
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
