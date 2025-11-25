package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

@Data
public class EventoResumidoDto {
    private int id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private String fecha;              // ej: "2025-11-10T11:00:00Z"
    private double precioEntrada;
    private EventoTipoDto eventoTipo;
}
