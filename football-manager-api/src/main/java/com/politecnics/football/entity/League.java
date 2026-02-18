package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;
    
    @Column(name = "season_year")
    private Integer seasonYear;

    // Could add list of teams here, but might be better to manage relationship from Team side or separate join
}
