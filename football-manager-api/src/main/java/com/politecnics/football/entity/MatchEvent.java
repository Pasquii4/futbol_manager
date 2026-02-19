package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Enumerated(EnumType.STRING)
    private EventType type; // GOAL, ASSIST, YELLOW_CARD, RED_CARD

    @Column(name = "event_minute")
    private Integer minute;
    
    public enum EventType {
        GOAL, ASSIST, YELLOW_CARD, RED_CARD
    }
}
