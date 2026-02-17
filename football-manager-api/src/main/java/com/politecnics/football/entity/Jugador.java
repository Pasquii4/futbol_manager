package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "jugadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Integer dorsal;

    @Enumerated(EnumType.STRING)
    private Posicion posicion;

    private Double calidad; // 0-100
    private Double fatiga;  // 0-100
    private Double sueldo;
    private Double motivacion;
    private Double forma;   // 0-10

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id")
    @ToString.Exclude
    private Equipo equipo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estadisticas_id")
    private EstadisticasJugador estadisticas;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesion_id")
    private Lesion lesion;
}
