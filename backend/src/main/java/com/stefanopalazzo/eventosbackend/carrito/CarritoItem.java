package com.stefanopalazzo.eventosbackend.carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoItem {
    private int eventoId;
    private int fila;
    private int columna;

    private Double precio;
}
