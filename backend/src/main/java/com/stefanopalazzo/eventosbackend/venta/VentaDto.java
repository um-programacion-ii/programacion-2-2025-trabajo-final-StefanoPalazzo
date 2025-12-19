package com.stefanopalazzo.eventosbackend.venta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaDto {
    private int eventoId;
    private Long ventaId;
    private String fechaVenta;
    private List<Map<String, Object>> asientos; // Flexible map to match JSON structure
    private boolean resultado;
    private String descripcion;
    private double precioVenta;
}
