package com.politecnics.football.service;

import com.politecnics.football.dto.PartidoDTO;
import com.politecnics.football.mapper.PartidoMapper;
import com.politecnics.football.repository.PartidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartidoService {
    private final PartidoRepository partidoRepository;
    private final PartidoMapper partidoMapper;

    public PartidoDTO getPartidoById(Long id) {
        return partidoRepository.findById(id)
                .map(partidoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Partido not found"));
    }
}
