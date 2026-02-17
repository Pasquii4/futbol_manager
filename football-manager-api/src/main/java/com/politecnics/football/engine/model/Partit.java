package com.politecnics.football.engine.model;

import java.time.LocalDateTime;
import java.util.*;

public class Partit {
    private Equip equip1;
    private Equip equip2;
    private int golsEquip1;
    private int golsEquip2;
    private int jornada;
    private boolean finalizado;
    private LocalDateTime fechaPartido;
    private List<EventoPartido> eventos;
    
    private Map<Jugador, Integer> goleadores = new HashMap<>();
    private Map<Jugador, Integer> asistentes = new HashMap<>();
    private Map<Jugador, Integer> tarjetasAmarillas = new HashMap<>();
    private Map<Jugador, Integer> tarjetasRojas = new HashMap<>();
    
    private double ratingEquip1 = 6.0;
    private double ratingEquip2 = 6.0;
    
    private static final Random random = new Random();

    public Partit(Equip equip1, Equip equip2, int jornada) {
        this.equip1 = equip1;
        this.equip2 = equip2;
        this.jornada = jornada;
        this.eventos = new ArrayList<>();
        this.fechaPartido = LocalDateTime.now();
    }
    
    public void disputar() {
        if (finalizado) return;
        eventos.clear();
        
        equip1.prepararPartido();
        equip2.prepararPartido();
        
        if (equip1.getJugadors().isEmpty() || equip2.getJugadors().isEmpty()) {
            finalizado = true;
            return;
        }
        
        // Registrar minuto jugado
        equip1.getTactica().getTitulares().forEach(j -> {
            j.registrarPartit();
        });
        equip2.getTactica().getTitulares().forEach(j -> {
            j.registrarPartit();
        });
        
        double fuerzaLocal = equip1.getTactica().calcularFuerzaTotal(true);
        double fuerzaVisitant = equip2.getTactica().calcularFuerzaTotal(false);
        
        if (equip1.getEntrenador() != null) {
            fuerzaLocal *= (1.0 + (equip1.getEntrenador().getExperiencia() / 200.0));
        }
        if (equip2.getEntrenador() != null) {
            fuerzaVisitant *= (1.0 + (equip2.getEntrenador().getExperiencia() / 200.0));
        }
        
        golsEquip1 = simularGolsPoisson(fuerzaLocal, fuerzaVisitant);
        golsEquip2 = simularGolsPoisson(fuerzaVisitant, fuerzaLocal);
        
        assignarGoleadoresYAsistencias(equip1, golsEquip1);
        assignarGoleadoresYAsistencias(equip2, golsEquip2);
        
        simularTarjetas(equip1);
        simularTarjetas(equip2);
        
        ratingEquip1 = calcularRatingEquipo(golsEquip1, golsEquip2);
        ratingEquip2 = calcularRatingEquipo(golsEquip2, golsEquip1);
        
        registrarEstadisticasEnJugadores();
        
        equip1.getTactica().aplicarFatigaPostPartido();
        equip2.getTactica().aplicarFatigaPostPartido();
        
        actualitzarFormaDespresPartit();
        
        eventos.sort(Comparator.comparingInt(EventoPartido::getMinuto));
        finalizado = true;
    }
    
    private int simularGolsPoisson(double fuerzaAtacante, double fuerzaDefensor) {
        double fuerzaNormAtacante = fuerzaAtacante / 100.0;
        double fuerzaNormDefensor = fuerzaDefensor / 100.0;
        double ratio = fuerzaNormAtacante / Math.max(0.3, fuerzaNormDefensor);
        double lambda = 0.7 + (ratio * 0.9);
        lambda = Math.min(3.5, Math.max(0.2, lambda));
        
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;
        
        while (p > L && k < 15) {
            k++;
            p *= random.nextDouble();
        }
        return Math.max(0, k - 1);
    }
    
    private void assignarGoleadoresYAsistencias(Equip equip, int gols) {
        List<Jugador> titulares = equip.getTactica().getTitulares();
        if (titulares.isEmpty()) return;
        
        for (int i = 0; i < gols; i++) {
            Jugador goleador = seleccionarGoleador(titulares);
            int minut = 1 + random.nextInt(90);
            
            goleador.registrarGol();
            goleadores.put(goleador, goleadores.getOrDefault(goleador, 0) + 1);
            
            Jugador assistent = null;
            if (random.nextDouble() < 0.70) {
                assistent = seleccionarAssistent(titulares, goleador);
                if (assistent != null) {
                    assistent.registrarAssistencia();
                    asistentes.put(assistent, asistentes.getOrDefault(assistent, 0) + 1);
                }
            }
            
            String descripcion = assistent != null 
                ? String.format("Gol de %s (Assist: %s)", goleador.getNom(), assistent.getNom())
                : String.format("Gol de %s", goleador.getNom());
            
            eventos.add(new EventoPartido(minut, EventoPartido.TipoEvento.GOL, goleador, descripcion));
        }
    }
    
    private Jugador seleccionarGoleador(List<Jugador> jugadores) {
        // Simplified selection logic
        return jugadores.get(random.nextInt(jugadores.size())); // Placeholder for complex logic if needed
    }
    
    private Jugador seleccionarAssistent(List<Jugador> jugadores, Jugador goleador) {
        List<Jugador> possibles = new ArrayList<>(jugadores);
        possibles.remove(goleador);
        if (possibles.isEmpty()) return null;
        return possibles.get(random.nextInt(possibles.size()));
    }
    
    private void simularTarjetas(Equip equip) {
        for (Jugador j : equip.getTactica().getTitulares()) {
            if (random.nextDouble() < 0.01) {
                j.registrarTargetaVermella();
                tarjetasRojas.put(j, tarjetasRojas.getOrDefault(j, 0) + 1);
                eventos.add(new EventoPartido(random.nextInt(90)+1, EventoPartido.TipoEvento.TARJETA_ROJA, j, "Roja a " + j.getNom()));
            } else if (random.nextDouble() < 0.05) {
                j.registrarTargetaGroga();
                tarjetasAmarillas.put(j, tarjetasAmarillas.getOrDefault(j, 0) + 1);
                eventos.add(new EventoPartido(random.nextInt(90)+1, EventoPartido.TipoEvento.TARJETA_AMARILLA, j, "Groga a " + j.getNom()));
            }
        }
    }
    
    private double calcularRatingEquipo(int favor, int contra) {
        double rating = 5.0 + (favor * 2.0) - (contra * 1.5);
        return Math.max(0, Math.min(10, rating));
    }
    
    private void registrarEstadisticasEnJugadores() {
        // Stats already updated in methods above for simplicity
        // But need to update ratings
        equip1.getTactica().getTitulares().forEach(j -> j.getEstadisticas().actualizarRating(6.5)); // Simplified
        equip2.getTactica().getTitulares().forEach(j -> j.getEstadisticas().actualizarRating(6.5));
    }
    
    private void actualitzarFormaDespresPartit() {
        boolean v1 = golsEquip1 > golsEquip2;
        boolean v2 = golsEquip2 > golsEquip1;
        boolean emp = golsEquip1 == golsEquip2;
        
        equip1.getTactica().getTitulares().forEach(j -> j.actualitzarFormaDespresPartit(v1, emp));
        equip2.getTactica().getTitulares().forEach(j -> j.actualitzarFormaDespresPartit(v2, emp));
    }

    // Getters
    public int getGolsEquip1() { return golsEquip1; }
    public int getGolsEquip2() { return golsEquip2; }
    public List<EventoPartido> getEventos() { return eventos; }
    public double getRatingEquip1() { return ratingEquip1; }
    public double getRatingEquip2() { return ratingEquip2; }
}
