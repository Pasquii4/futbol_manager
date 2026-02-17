package com.politecnics.football.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presupuesto {
    private Double saldo;
    private Double saldoInicial;
    private Double ingresosPorPartido;
    private Double gastosMantenimiento;
}
