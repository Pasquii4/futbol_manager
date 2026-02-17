package com.politecnics.football.dto;

public record EstadisticasJugadorDTO(
    Integer goles,
    Integer asistencias,
    Integer tarjetasAmarillas,
    Integer tarjetasRojas,
    Integer partidosJugados,
    Integer minutosJugados,
    Double ratingPromedio,
    Integer paradas,
    Integer golesRecibidos
) {}
