package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer diasRestantes;
    private String tipoLesion;

    @OneToOne(mappedBy = "lesion")
    @ToString.Exclude
    private Jugador jugador;
}
