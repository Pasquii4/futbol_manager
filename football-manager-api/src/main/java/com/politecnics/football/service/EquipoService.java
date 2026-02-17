package com.politecnics.football.service;

import com.politecnics.football.dto.EquipoDTO;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.mapper.EquipoMapper;
import com.politecnics.football.repository.EquipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipoService {
    private final EquipoRepository equipoRepository;
    private final EquipoMapper equipoMapper;

    public List<EquipoDTO> getAllEquipos() {
        return equipoRepository.findAll().stream()
                .map(equipoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EquipoDTO getEquipoById(Long id) {
        return equipoRepository.findById(id)
                .map(equipoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Equipo not found"));
    }
    
    public List<EquipoDTO> getEquiposByLiga(Long ligaId) {
        return equipoRepository.findByLigaId(ligaId).stream()
                .map(equipoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
