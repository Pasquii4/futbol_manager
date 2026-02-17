package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "entrenadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrenador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Double motivacion;
    private Double sueldoAnual;
    
    // Attributes specific to Trainer
    private Integer torneosGanados;
    private Boolean seleccionadorNacional;

    @OneToOne(mappedBy = "entrenador")
    @ToString.Exclude
    private Equipo equipo;
}
