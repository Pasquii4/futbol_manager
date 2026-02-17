package com.politecnics.football.service;

import com.politecnics.football.dto.LigaDTO;
import com.politecnics.football.dto.PartidoDTO;
import com.politecnics.football.entity.Liga;
import com.politecnics.football.entity.Partido;
import com.politecnics.football.mapper.LigaMapper;
import com.politecnics.football.mapper.PartidoMapper;
import com.politecnics.football.repository.LigaRepository;
import com.politecnics.football.repository.PartidoRepository;
import com.politecnics.football.engine.adapter.MotorSimulacionAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LigaService {
    private final LigaRepository ligaRepository;
    private final PartidoRepository partidoRepository;
    private final LigaMapper ligaMapper;
    private final PartidoMapper partidoMapper;
    private final MotorSimulacionAdapter motorSimulacionAdapter;

    public List<LigaDTO> getAllLigas() {
        return ligaRepository.findAll().stream()
                .map(ligaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public LigaDTO getLigaById(Long id) {
        return ligaRepository.findById(id)
                .map(ligaMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Liga not found"));
    }

    @Transactional
    public List<PartidoDTO> simularJornada(Long ligaId) {
        Liga liga = ligaRepository.findById(ligaId)
                .orElseThrow(() -> new RuntimeException("Liga not found"));
        
        Integer jornadaActual = liga.getJornadaActual();
        if (jornadaActual > liga.getTotalJornadas()) {
            throw new RuntimeException("Liga finalizada");
        }
        
        List<Partido> partidosJornada = partidoRepository.findByLigaIdAndJornada(ligaId, jornadaActual);
        
        for (Partido partido : partidosJornada) {
            if (!partido.getJugado()) {
                motorSimulacionAdapter.simularPartido(partido);
                partidoRepository.save(partido);
            }
        }
        
        // Advance journey
        liga.setJornadaActual(jornadaActual + 1);
        if (liga.getJornadaActual() > liga.getTotalJornadas()) {
            liga.setFinalizada(true);
        }
        ligaRepository.save(liga);
        
        return partidosJornada.stream()
                .map(partidoMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<PartidoDTO> getResultadosJornada(Long ligaId, Integer jornada) {
        return partidoRepository.findByLigaIdAndJornada(ligaId, jornada).stream()
                .map(partidoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
