package com.politecnics.football.repository;

import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    List<Partido> findByLigaIdAndJornada(Long ligaId, Integer jornada);
    List<Partido> findByLigaIdOrderByJornadaAsc(Long ligaId);
    
    // Find matches involving a specific team
    List<Partido> findByEquipoLocalOrEquipoVisitante(Equipo local, Equipo visitante);
}
