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
        if (titulares == null || titulares.isEmpty()) return;
        
        for (int i = 0; i < gols; i++) {
            Jugador goleador = seleccionarGoleador(titulares);
            if (goleador == null) continue;
            
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
        if (jugadores == null || jugadores.isEmpty()) return null;

        double totalWeight = jugadores.stream()
            .mapToDouble(j -> j.getPesoGol() * (j.getQualitat() / 10.0))
            .sum();
            
        double r = random.nextDouble() * totalWeight;
        double currentWeight = 0.0;
        
        for (Jugador j : jugadores) {
            currentWeight += j.getPesoGol() * (j.getQualitat() / 10.0);
            if (r <= currentWeight) {
                return j;
            }
        }
        return jugadores.get(random.nextInt(jugadores.size())); // Fallback
    }
    
    private Jugador seleccionarAssistent(List<Jugador> jugadores, Jugador goleador) {
        List<Jugador> possibles = new ArrayList<>(jugadores);
        possibles.remove(goleador);
        if (possibles.isEmpty()) return null;
        
        // Simple weighted selection for assists (Midfielders have higher chance)
        double totalWeight = possibles.stream()
            .mapToDouble(j -> j.getPosicio().equals("MIG") ? 3.0 : 1.0)
            .sum();
            
        double r = random.nextDouble() * totalWeight;
        double currentWeight = 0.0;
        
        for (Jugador j : possibles) {
            currentWeight += j.getPosicio().equals("MIG") ? 3.0 : 1.0;
            if (r <= currentWeight) return j;
        }
        
        return possibles.get(random.nextInt(possibles.size()));
    }
    
    private void simularTarjetas(Equip equip) {
        List<Jugador> titulares = equip.getTactica().getTitulares();
        if (titulares == null || titulares.isEmpty()) return;

        for (Jugador j : titulares) {
            double chanceFactor = j.getPesoTarjetas(); // Base on position
            
            if (random.nextDouble() < (0.005 * chanceFactor)) {
                j.registrarTargetaVermella();
                tarjetasRojas.put(j, tarjetasRojas.getOrDefault(j, 0) + 1);
                eventos.add(new EventoPartido(random.nextInt(90)+1, EventoPartido.TipoEvento.TARJETA_ROJA, j, "Roja a " + j.getNom()));
            } else if (random.nextDouble() < (0.05 * chanceFactor)) {
                j.registrarTargetaGroga();
                tarjetasAmarillas.put(j, tarjetasAmarillas.getOrDefault(j, 0) + 1);
                eventos.add(new EventoPartido(random.nextInt(90)+1, EventoPartido.TipoEvento.TARJETA_AMARILLA, j, "Groga a " + j.getNom()));
            }
        }
    }
    
    private double calcularRatingEquipo(int favor, int contra) {
        double rating = 6.0 + (favor * 0.5) - (contra * 0.3);
        if (favor > contra) rating += 1.0;
        return Math.max(0, Math.min(10, rating));
    }
    
    private void registrarEstadisticasEnJugadores() {
        // Calculate dynamic ratings
        actualizarRatingEquipo(equip1, golsEquip1, golsEquip2);
        actualizarRatingEquipo(equip2, golsEquip2, golsEquip1);
        
        // Simulate Injuries
        simularLesiones(equip1);
        simularLesiones(equip2);
    }
    
    private void actualizarRatingEquipo(Equip equip, int gf, int gc) {
        List<Jugador> titulares = equip.getTactica().getTitulares();
        if (titulares == null || titulares.isEmpty()) return;

        boolean victory = gf > gc;
        boolean draw = gf == gc;
        boolean cleanSheet = gc == 0;
        
        for (Jugador j : titulares) {
            double rating = 6.0;
            
            // Base based heavily on quality for realism foundation
            rating += (j.getQualitat() - 60.0) / 20.0; // Bonus for better players
            
            // Events
            int goles = goleadores.getOrDefault(j, 0);
            int asis = asistentes.getOrDefault(j, 0);
            
            rating += (goles * 1.5);
            rating += (asis * 1.0);
            
            if (j.getPosicio().equals("POR") || j.getPosicio().equals("DEF")) {
                if (cleanSheet) rating += 1.5;
                else rating -= (gc * 0.3);
            }
            
            if (victory) rating += 0.5;
            if (!draw && !victory) rating -= 0.5;
            
            j.getEstadisticas().actualizarRating(Math.max(3.0, Math.min(10.0, rating)));
        }
    }
    
    private void simularLesiones(Equip equip) {
        List<Jugador> titulares = equip.getTactica().getTitulares();
        if (titulares == null || titulares.isEmpty()) return;

        for (Jugador j : titulares) {
            // 1% chance of injury
            if (random.nextDouble() < 0.01) {
                // Determine severity (1-5 weeks)
                int weeks = 1 + random.nextInt(4);
                // We create a Lesion object (assuming engine model suppports it or we set flag)
                j.setLesionActual(new Lesion("Lesión Muscular", weeks * 7));
                eventos.add(new EventoPartido(random.nextInt(90)+1, EventoPartido.TipoEvento.LESION, j, "Lesión de " + j.getNom()));
            }
        }
    }
    
    private void actualitzarFormaDespresPartit() {
        boolean v1 = golsEquip1 > golsEquip2;
        boolean v2 = golsEquip2 > golsEquip1;
        boolean emp = golsEquip1 == golsEquip2;
        
        if (equip1.getTactica().getTitulares() != null)
             equip1.getTactica().getTitulares().forEach(j -> j.actualitzarFormaDespresPartit(v1, emp));
             
        if (equip2.getTactica().getTitulares() != null)
             equip2.getTactica().getTitulares().forEach(j -> j.actualitzarFormaDespresPartit(v2, emp));
    }

    // Getters
    public int getGolsEquip1() { return golsEquip1; }
    public int getGolsEquip2() { return golsEquip2; }
    public List<EventoPartido> getEventos() { return eventos; }
    public double getRatingEquip1() { return ratingEquip1; }
    public double getRatingEquip2() { return ratingEquip2; }
}
