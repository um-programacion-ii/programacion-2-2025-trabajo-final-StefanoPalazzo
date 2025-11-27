package com.stefanopalazzo.eventosbackend.carrito;

import lombok.Data;

@Data
public class QuitarAsientoRequest {
    private int eventoId;
    private int fila;
    private int columna;
}
