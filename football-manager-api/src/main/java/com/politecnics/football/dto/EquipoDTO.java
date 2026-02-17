package com.politecnics.football.dto;

import java.util.List;

public record EquipoDTO(
    Long id,
    String nombre,
    Integer anyFundacion,
    String ciudad,
    String estadio,
    String presidente,
    Integer puntos,
    Integer golesFavor,
    Integer golesContra,
    Integer partidosJugados,
    Integer victorias,
    Integer empates,
    Integer derrotas,
    EntrenadorDTO entrenador,
    TacticaDTO tactica,
    List<JugadorDTO> jugadores
) {}
