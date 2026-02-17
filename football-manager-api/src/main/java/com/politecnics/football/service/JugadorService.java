package com.politecnics.football.service;

import com.politecnics.football.dto.JugadorDTO;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.mapper.JugadorMapper;
import com.politecnics.football.repository.JugadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JugadorService {
    private final JugadorRepository jugadorRepository;
    private final JugadorMapper jugadorMapper;

    public JugadorDTO getJugadorById(Long id) {
        return jugadorRepository.findById(id)
                .map(jugadorMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Jugador not found"));
    }

    public List<JugadorDTO> getJugadoresByEquipo(Long equipoId) {
        return jugadorRepository.findByEquipoId(equipoId).stream()
                .map(jugadorMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<JugadorDTO> getMercadoFichajes() {
        return jugadorRepository.findByEquipoIsNull().stream()
                .map(jugadorMapper::toDTO)
                .collect(Collectors.toList());
    }
}
