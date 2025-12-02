package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventoCompletoDto {
    private int id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private String fecha;
    private double precioEntrada;
    private EventoTipoDto eventoTipo;

    private String direccion;
    private String imagen;
    private int filaAsientos;
    private int columnaAsientos;

    private List<IntegranteDto> integrantes;
}
