package com.politecnics.football.dto;

import com.politecnics.football.entity.Posicion;

public record JugadorDTO(
    Long id,
    String nombre,
    String apellido,
    Integer dorsal,
    Posicion posicion,
    Double calidad,
    Double fatiga,
    Double forma,
    Double sueldo,
    Double motivacion,
    LesionDTO lesion,
    EstadisticasJugadorDTO estadisticas
) {
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
