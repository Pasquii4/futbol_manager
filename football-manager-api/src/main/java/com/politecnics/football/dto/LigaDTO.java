package com.politecnics.football.dto;

import java.util.List;

public record LigaDTO(
    Long id,
    String nombre,
    Integer jornadaActual,
    Integer totalJornadas,
    Boolean finalizada,
    List<EquipoDTO> equipos
) {}
