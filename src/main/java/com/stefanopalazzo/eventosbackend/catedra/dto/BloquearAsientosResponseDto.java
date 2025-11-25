package com.stefanopalazzo.eventosbackend.catedra.dto;

import lombok.Data;

import java.util.List;

@Data
public class BloquearAsientosResponseDto {
    private boolean resultado;
    private String descripcion;
    private int eventoId;
    private List<AsientoDto> asientos;
}
