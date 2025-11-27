package com.stefanopalazzo.eventosbackend.asiento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    List<Asiento> findByEventoId(int eventoId);

    Optional<Asiento> findByEventoIdAndFilaAndColumna(int eventoId, int fila, int columna);
}
