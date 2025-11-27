package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

@Data
public class QuitarItemDto {
    private int eventoId;
    private int fila;
    private int columna;
}
