package com.politecnics.football.service;

import com.politecnics.football.dto.PlayerDTO;
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

    public PlayerDTO getJugadorById(Long id) {
        return jugadorRepository.findById(id)
                .map(jugadorMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Jugador not found"));
    }

    public List<PlayerDTO> getJugadoresByEquipo(Long equipoId) {
        return jugadorRepository.findByEquipoId(equipoId).stream()
                .map(jugadorMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<PlayerDTO> getMercadoFichajes() {
        return jugadorRepository.findByEquipoIsNull().stream()
                .map(jugadorMapper::toDTO)
                .collect(Collectors.toList());
    }
}
