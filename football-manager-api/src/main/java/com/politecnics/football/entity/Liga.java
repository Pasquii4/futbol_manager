package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ligas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private Integer jornadaActual;
    private Integer totalJornadas;
    private Boolean finalizada;

    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Equipo> equipos = new ArrayList<>();

    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Partido> partidos = new ArrayList<>();
}
