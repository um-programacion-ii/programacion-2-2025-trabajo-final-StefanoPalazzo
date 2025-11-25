package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

@Data
public class AsientoDto {
    private int fila;
    private int columna;
    private String estado; // "Ocupado", "Libre", "Bloqueo exitoso", "Vendido"
    private String persona; // solo para ventas
}
