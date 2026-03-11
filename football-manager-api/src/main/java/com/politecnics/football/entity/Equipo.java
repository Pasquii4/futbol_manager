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

    @Column(unique = true)
    private String teamId;

    private Integer anyFundacion;
    private String ciudad;
    private String estadio;
    private String presidente;
    
    @Column(name = "overall_rating")
    private Integer calidadRating;

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
    
    // --- Adapter methods for Team backward compatibility ---
    public String getStadium() { return this.estadio; }
    public void setStadium(String stadium) { this.estadio = stadium; }
    
    public Long getBudget() { 
        return this.presupuesto != null && this.presupuesto.getSaldo() != null ? this.presupuesto.getSaldo().longValue() : 0L; 
    }
    public void setBudget(Long budget) { 
        if(this.presupuesto == null) this.presupuesto = new Presupuesto(0.0, 0.0, 0.0, 0.0);
        this.presupuesto.setSaldo(budget != null ? budget.doubleValue() : 0.0);
    }
    
    public String getFormation() {
        return (this.tactica != null && this.tactica.getFormacion() != null) ? this.tactica.getFormacion().name() : "4-3-3";
    }
    public void setFormation(String formation) {
        if(this.tactica == null) this.tactica = new Tactica();
        /* not supported yet */
    }
    
    public String getMentality() {
        return "Equilibrada";
    }
    public void setMentality(String mentality) {
        if(this.tactica == null) this.tactica = new Tactica();
        /* not supported yet */
    }
}
