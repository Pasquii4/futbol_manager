package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tacticas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tactica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Formacion formacion;

    @Enumerated(EnumType.STRING)
    private EstiloJuego estiloJuego;

    private Integer intensidadPresion;
}
