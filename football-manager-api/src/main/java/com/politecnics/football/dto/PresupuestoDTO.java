package com.politecnics.football.dto;

public record PresupuestoDTO(
    Double saldo,
    Double saldoInicial,
    Double ingresosPorPartido,
    Double gastosMantenimiento
) {}
