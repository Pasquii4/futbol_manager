package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal DB ID

    @Column(unique = true)
    private String teamId; // String ID from JSON (e.g., "real_madrid")

    private String name;
    private String stadium;
    private Long budget;
    
    @Column(name = "overall_rating")
    private Integer overallRating;

    @Builder.Default
    private String formation = "4-3-3";
    
    @Builder.Default
    private String mentality = "Balanced";


    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players;
}
