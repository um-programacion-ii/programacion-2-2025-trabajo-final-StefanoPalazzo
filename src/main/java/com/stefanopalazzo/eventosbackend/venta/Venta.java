package com.stefanopalazzo.eventosbackend.venta;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int eventoId;
    private String itemsJson;   // lista de asientos vendidos serializada
    private double total;
    private String fecha;
}
