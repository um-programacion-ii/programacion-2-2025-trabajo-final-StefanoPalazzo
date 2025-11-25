package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

import java.util.List;

@Data
public class VentaDetalleDto {
    private int eventoId;
    private int ventaId;
    private String fechaVenta;
    private boolean resultado;
    private String descripcion;
    private double precioVenta;
    private List<AsientoDto> asientos;
}
