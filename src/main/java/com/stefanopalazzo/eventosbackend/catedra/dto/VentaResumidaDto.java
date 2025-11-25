package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

@Data
public class VentaResumidaDto {
    private int eventoId;
    private int ventaId;
    private String fechaVenta;
    private boolean resultado;
    private String descripcion;
    private double precioVenta;
    private int cantidadAsientos;
}
