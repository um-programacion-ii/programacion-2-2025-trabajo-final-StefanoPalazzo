package com.stefanopalazzo.eventosbackend.asiento;

import lombok.Data;

@Data
public class AsientoBloqueoRequest {
    private int eventoId;
    private int fila;
    private int columna;
}
