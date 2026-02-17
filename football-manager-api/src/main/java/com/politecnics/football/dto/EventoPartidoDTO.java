package com.politecnics.football.dto;

import com.politecnics.football.entity.EventoPartido;

public record EventoPartidoDTO(
    Long id,
    Integer minuto,
    EventoPartido.TipoEvento tipo,
    String descripcion,
    String nombreJugador
) {}
