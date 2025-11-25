package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

import java.util.List;

@Data
public class VentaRequestDto {
    private int eventoId;
    private String fecha;             // "2025-08-17T20:00:00.000Z"
    private double precioVenta;
    private List<AsientoVentaDto> asientos;

    @Data
    public static class AsientoVentaDto {
        private int fila;
        private int columna;
        private String persona;  // nombre de la persona que compra
    }
}
