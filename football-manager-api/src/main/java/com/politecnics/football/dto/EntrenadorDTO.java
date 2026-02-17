package com.politecnics.football.dto;

public record EntrenadorDTO(
    Long id,
    String nombre,
    String apellido,
    Integer torneosGanados,
    Boolean seleccionadorNacional,
    Double sueldoAnual
) {}
