package com.politecnics.football.dto;

import com.politecnics.football.entity.EstiloJuego;
import com.politecnics.football.entity.Formacion;

public record TacticaDTO(
    Long id,
    Formacion formacion,
    EstiloJuego estiloJuego,
    Integer intensidadPresion
) {}
