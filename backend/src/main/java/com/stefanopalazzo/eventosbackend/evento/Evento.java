package com.stefanopalazzo.eventosbackend.evento;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eventos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Evento {

    @Id
    private Integer id;

    private String titulo;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String resumen;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;


    private String fecha;

    private double precioEntrada;

    private String tipo;

    private String direccion;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String imagen;

    private int filaAsientos;
    @Column(name = "columna_asientos")
    private int columnAsientos;

    private String ultimaActualizacion;
}
