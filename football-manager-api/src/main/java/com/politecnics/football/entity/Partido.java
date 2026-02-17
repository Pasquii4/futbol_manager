package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "partidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer jornada;
    private Boolean jugado;
    private LocalDateTime fechaPartido;

    @ManyToOne
    @JoinColumn(name = "liga_id")
    private Liga liga;

    @ManyToOne
    @JoinColumn(name = "equipo_local_id")
    private Equipo equipoLocal;

    @ManyToOne
    @JoinColumn(name = "equipo_visitante_id")
    private Equipo equipoVisitante;

    private Integer golesLocal;
    private Integer golesVisitante;
    
    private Double ratingLocal;
    private Double ratingVisitante;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventoPartido> eventos = new ArrayList<>();
}
