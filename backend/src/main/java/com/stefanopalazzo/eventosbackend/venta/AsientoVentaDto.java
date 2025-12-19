package com.stefanopalazzo.eventosbackend.venta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoVentaDto {
    private int fila;
    private int columna;
    private String persona;
}
