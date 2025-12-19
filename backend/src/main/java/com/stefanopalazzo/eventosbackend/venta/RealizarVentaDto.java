package com.stefanopalazzo.eventosbackend.venta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealizarVentaDto {
    private Long eventoId;
    private String fecha;
    private Double precioVenta;
    private List<AsientoVentaDto> asientos;
}
