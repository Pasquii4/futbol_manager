package com.politecnics.football.repository;

import com.politecnics.football.entity.Liga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LigaRepository extends JpaRepository<Liga, Long> {
    Optional<Liga> findByNombre(String nombre);
    List<Liga> findAllByOrderByIdDesc();
    
    // Find active leagues (not finalized)
    List<Liga> findByFinalizadaFalse();
}
