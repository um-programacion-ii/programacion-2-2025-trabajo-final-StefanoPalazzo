package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

import java.util.List;

@Data
public class VentaResponseDto {
    private int eventoId;
    private Integer ventaId;   // puede venir null
    private String fechaVenta;
    private boolean resultado;
    private String descripcion;
    private double precioVenta;
    private List<AsientoDto> asientos;
}
