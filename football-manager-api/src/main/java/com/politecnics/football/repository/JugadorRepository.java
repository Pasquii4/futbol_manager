package com.politecnics.football.repository;

import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Posicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    List<Jugador> findByEquipoId(Long equipoId);
    
    // Transfer market: players without a team
    List<Jugador> findByEquipoIsNull();
    
    List<Jugador> findByPosicion(Posicion posicion);
    
    // Find free agents by position
    List<Jugador> findByEquipoIsNullAndPosicion(Posicion posicion);
}
