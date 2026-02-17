package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eventos_partido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPartido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer minuto;
    
    @Enumerated(EnumType.STRING)
    private TipoEvento tipo;
    
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "partido_id")
    @ToString.Exclude
    private Partido partido;

    @ManyToOne
    @JoinColumn(name = "jugador_id")
    private Jugador jugador;
    
    public enum TipoEvento {
        GOL,
        ASISTENCIA,
        TARJETA_AMARILLA,
        TARJETA_ROJA,
        LESION,
        SUSTITUCION
    }
}
