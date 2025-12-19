package com.stefanopalazzo.eventosbackend.asiento;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asientos")
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int eventoId;
    private int fila;
    private int columna;

    @Enumerated(EnumType.STRING)
    private AsientoEstado estado;

    private double precio;

    private String persona;
}
