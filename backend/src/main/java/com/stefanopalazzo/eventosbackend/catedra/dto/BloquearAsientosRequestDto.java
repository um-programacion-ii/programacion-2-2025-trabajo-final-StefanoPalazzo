package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

import java.util.List;

@Data
public class BloquearAsientosRequestDto {
    private int eventoId;
    private List<AsientoRequestDto> asientos;

    @Data
    public static class AsientoRequestDto {
        private int fila;
        private int columna;
    }
}
