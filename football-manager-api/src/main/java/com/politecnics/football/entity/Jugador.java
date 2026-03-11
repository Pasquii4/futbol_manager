package com.politecnics.football.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(unique = true)
    private String jugadorId; // String ID from JSON

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

    @Column(name = "market_value")
    private Long marketValue; // Value in Euro

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id")
    @ToString.Exclude
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore // Prevent infinite recursion in default serialization
    @ToString.Exclude
    private Equipo team;

    // Stats fields
    @Builder.Default
    private Integer goalsScored = 0;
    @Builder.Default
    private Integer assists = 0;
    @Builder.Default
    private Integer yellowCards = 0;
    @Builder.Default
    private Integer redCards = 0;
    @Builder.Default
    private Integer matchesPlayed = 0;

    // Simulation fields
    @Column(name = "injured")
    @Builder.Default
    private Boolean injured = false;

    @Column(name = "injury_duration")
    @Builder.Default
    private Integer injuryDuration = 0; // Matchdays remaining

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estadisticas_id")
    private EstadisticasJugador estadisticas;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesion_id")
    private Lesion lesion;
}
