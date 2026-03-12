package com.politecnics.football.service;

import com.politecnics.football.dto.LigaDTO;
import com.politecnics.football.dto.MatchDTO;
import com.politecnics.football.entity.Liga;
import com.politecnics.football.entity.Match;
import com.politecnics.football.mapper.LigaMapper;
import com.politecnics.football.mapper.MatchMapper;
import com.politecnics.football.repository.LigaRepository;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.service.simulation.MatchEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LigaService {
    private final LigaRepository ligaRepository;
    private final MatchRepository matchRepository;
    private final LigaMapper ligaMapper;
    private final MatchMapper matchMapper;
    private final MatchEngine matchEngine;

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
    public List<MatchDTO> simularJornada(Long ligaId) {
        Liga liga = ligaRepository.findById(ligaId)
                .orElseThrow(() -> new RuntimeException("Liga not found"));
        
        Integer jornadaActual = liga.getJornadaActual();
        if (jornadaActual > liga.getTotalJornadas()) {
            throw new RuntimeException("Liga finalizada");
        }
        
        List<Match> matchesJornada = matchRepository.findByMatchday(jornadaActual); // Assuming it finds matches for this liga, wait, we need findByLigaIdAndMatchday!
        // We need to implement findByLigaIdAndMatchday in MatchRepository or filter it manually
        // Since we didn't add the method yet, we can filter manually for now:
        matchesJornada = matchesJornada.stream()
            .filter(m -> m.getLiga() != null && m.getLiga().getId().equals(ligaId))
            .collect(Collectors.toList());
        
        for (Match match : matchesJornada) {
            if (!match.isPlayed()) {
                matchEngine.simulateMatch(match, null);
                matchRepository.save(match);
            }
        }
        
        // Advance journey
        liga.setJornadaActual(jornadaActual + 1);
        if (liga.getJornadaActual() > liga.getTotalJornadas()) {
            liga.setFinalizada(true);
        }
        ligaRepository.save(liga);
        
        return matchesJornada.stream()
                .map(matchMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<MatchDTO> getResultadosJornada(Long ligaId, Integer jornada) {
        return matchRepository.findByMatchday(jornada).stream()
                .filter(m -> m.getLiga() != null && m.getLiga().getId().equals(ligaId))
                .map(matchMapper::toDTO)
                .collect(Collectors.toList());
    }
}
