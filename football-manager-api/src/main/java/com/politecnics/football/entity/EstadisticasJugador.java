package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estadisticas_jugador")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasJugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Integer goles = 0;
    
    @Builder.Default
    private Integer asistencias = 0;
    
    @Builder.Default
    private Integer tarjetasAmarillas = 0;
    
    @Builder.Default
    private Integer tarjetasRojas = 0;
    
    @Builder.Default
    private Integer partidosJugados = 0;
    
    @Builder.Default
    private Integer minutosJugados = 0;
    
    @Builder.Default
    private Double ratingPromedio = 6.0;
    
    // Goalkeeper stats
    @Builder.Default
    private Integer paradas = 0;
    
    @Builder.Default
    private Integer golesRecibidos = 0;

    @OneToOne(mappedBy = "estadisticas")
    @ToString.Exclude
    private Jugador jugador;
}
