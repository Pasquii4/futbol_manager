package com.politecnics.football.dto;

import java.util.List;

public record PartidoDTO(
    Long id,
    Integer jornada,
    String equipoLocal,
    String equipoVisitante,
    Integer golesLocal,
    Integer golesVisitante,
    Boolean jugado,
    List<EventoPartidoDTO> eventos
) {}
