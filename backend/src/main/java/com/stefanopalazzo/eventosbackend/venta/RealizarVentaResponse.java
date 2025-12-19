package com.stefanopalazzo.eventosbackend.venta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealizarVentaResponse {
    private Long ventaId;
    private Long eventoId;
    private String fechaVenta;
    private Double precioVenta;
    private boolean resultado;
    private String descripcion;
    private java.util.List<com.stefanopalazzo.eventosbackend.asiento.Asiento> asientos;
}
