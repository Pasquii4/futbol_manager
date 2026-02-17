package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "equipos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private Integer anyFundacion;
    private String ciudad;
    private String estadio;
    private String presidente;

    @Builder.Default
    private Integer puntos = 0;
    @Builder.Default
    private Integer golesFavor = 0;
    @Builder.Default
    private Integer golesContra = 0;
    @Builder.Default
    private Integer partidosJugados = 0;
    @Builder.Default
    private Integer victorias = 0;
    @Builder.Default
    private Integer empates = 0;
    @Builder.Default
    private Integer derrotas = 0;
    


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liga_id")
    @ToString.Exclude
    private Liga liga;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Jugador> jugadores = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tactica_id")
    private Tactica tactica;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;
    
    @Embedded
    private Presupuesto presupuesto;
}
