package com.politecnics.football.dto;

public record ClasificacionDTO(
    Integer posicion,
    String equipo,
    Integer puntos,
    Integer partidosJugados,
    Integer ganados,
    Integer empatados,
    Integer perdidos,
    Integer golesAFavor,
    Integer golesEnContra,
    Integer diferencia
) {}
