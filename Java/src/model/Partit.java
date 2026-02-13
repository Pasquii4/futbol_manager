package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Classe que representa un partit entre dos equips.
 * Simula el resultat amb tàctiques, estad\u00edstiques detallades i esdeveniments.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class Partit {
    private Equip equip1; // Equip local
    private Equip equip2; // Equip visitant
    private int golsEquip1;
    private int golsEquip2;
    
    // Nous camps per Phase 5
    private LocalDateTime fechaPartido;
    private int jornada;
    private boolean finalizado;
    private List<EventoPartido> eventos;
    private Map<Jugador, Integer> goleadores; // Jugador → nombre de gols
    private Map<Jugador, Integer> asistentes;  // Jugador → nombre d'assistències
    private Map<Jugador, Integer> tarjetasAmarillas;
    private Map<Jugador, Integer> tarjetasRojas;
    private double ratingEquip1;
    private double ratingEquip2;
    
    private List<String> cronologia; // Mantenim per compatibilitat
    private static final Random random = new Random();

    /**
     * Constructor del partit.
     * 
     * @param equip1 Equip local
     * @param equip2 Equip visitant
     */
    public Partit(Equip equip1, Equip equip2) {
        this(equip1, equip2, 1);
    }
    
    /**
     * Constructor amb jornada.
     */
    public Partit(Equip equip1, Equip equip2, int jornada) {
        this.equip1 = equip1;
        this.equip2 = equip2;
        this.jornada = jornada;
        this.golsEquip1 = 0;
        this.golsEquip2 = 0;
        this.finalizado = false;
        this.fechaPartido = LocalDateTime.now();
        
        this.eventos = new ArrayList<>();
        this.goleadores = new HashMap<>();
        this.asistentes = new HashMap<>();
        this.tarjetasAmarillas = new HashMap<>();
        this.tarjetasRojas = new HashMap<>();
        this.cronologia = new ArrayList<>();
        
        this.ratingEquip1 = 6.0;
        this.ratingEquip2 = 6.0;
    }

    /**
     * Disputa el partit utilitzant el sistema tàctic complet.
     * Integra formacions, estils de joc, estadístiques i esdeveniments.
     */
    public void disputar() {
        if (finalizado) {
            return; // Partit ja jugat
        }
        
        eventos.clear();
        cronologia.clear();
        
        // PAS 1: Preparar equipos (generar alineacions)
        equip1.prepararPartido();
        equip2.prepararPartido();
        
        // Verificar que tenen jugadors
        if (equip1.getJugadors().isEmpty() || equip2.getJugadors().isEmpty()) {
            // Walkover: 0-0 si algun equip no té jugadors
            golsEquip1 = 0;
            golsEquip2 = 0;
            finalizado = true;
            return;
        }
        
        // Marcar tots els jugadors titulars com que han jugat
        for (Jugador j : equip1.getTactica().getTitulares()) {
            j.registrarPartit();
            j.getEstadisticas().registrarPartido(90); // 90 minuts
        }
        for (Jugador j : equip2.getTactica().getTitulares()) {
            j.registrarPartit();
            j.getEstadisticas().registrarPartido(90);
        }
        
        // PAS 2: Calcular forces amb tàctiques
        double fuerzaLocal = equip1.getTactica().calcularFuerzaTotal(true); // Local
        double fuerzaVisitant = equip2.getTactica().calcularFuerzaTotal(false); // Fora
        
        // Aplicar factor entrenador si existeix (usant experiència del camp)
        if (equip1.getEntrenador() != null) {
            double factorEntrenador = 1.0 + (equip1.getEntrenador().getExperiencia() / 200.0);
            fuerzaLocal *= factorEntrenador;
        }
        if (equip2.getEntrenador() != null) {
            double factorEntrenador = 1.0 + (equip2.getEntrenador().getExperiencia() / 200.0);
            fuerzaVisitant *= factorEntrenador;
        }
        
        // PAS 3: Simular gols amb distribució Poisson
        golsEquip1 = simularGolsPoisson(fuerzaLocal, fuerzaVisitant);
        golsEquip2 = simularGolsPoisson(fuerzaVisitant, fuerzaLocal);
        
        // PAS 4: Assignar goleadors segons probabilitat per posició
        assignarGoleadoresYAsistencias(equip1, golsEquip1, true);
        assignarGoleadoresYAsistencias(equip2, golsEquip2, false);
        
        // PAS 5: Simular targetes
        simularTarjetas(equip1);
        simularTarjetas(equip2);
        
        // PAS 6: Calcular ratings d'equip
        ratingEquip1 = calcularRatingEquipo(golsEquip1, golsEquip2);
        ratingEquip2 = calcularRatingEquipo(golsEquip2, golsEquip1);
        
        // PAS 7: Registrar estadístiques en jugadors
        registrarEstadisticasEnJugadores();
        
        // PAS 8: Aplicar fatiga post-partit
        equip1.getTactica().aplicarFatigaPostPartido();
        equip2.getTactica().aplicarFatigaPostPartido();
        
        // PAS 9: Actualitzar forma dels jugadors
        actualitzarFormaDespresPartit();
        
        // PAS 10: Ordenar esdeveniments per minut
        eventos.sort((e1, e2) -> Integer.compare(e1.getMinuto(), e2.getMinuto()));
        
        finalizado = true;
    }
    
    /**
     * Simula gols amb distribució de Poisson (AJUSTADA per realisme).
     */
    private int simularGolsPoisson(double fuerzaAtacante, double fuerzaDefensor) {
        // NORMALITZAR: dividir per 100 per portar a escala 0-1
        double fuerzaNormAtacante = fuerzaAtacante / 100.0;
        double fuerzaNormDefensor = fuerzaDefensor / 100.0;
        
        // Ratio normalitzat (típicament entre 0.5 i 2.0)
        double ratio = fuerzaNormAtacante / Math.max(0.3, fuerzaNormDefensor);
        
        // Lambda ajustat per partits de futbol reals (0.5 a 3.0 gols esperats)
        // Si ratio = 1.0 (equilibri) → lambda ≈ 1.3 gols
        // Si ratio = 2.0 (molt superior) → lambda ≈ 2.5 gols
        double lambda = 0.7 + (ratio * 0.9);
        lambda = Math.min(3.5, Math.max(0.2, lambda)); // Límits realistes
        
        // Distribució de Poisson
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;
        
        while (p > L && k < 15) { // Límit de seguretat: màxim 15 gols
            k++;
            p *= random.nextDouble();
        }
        
        return Math.max(0, k - 1);
    }
    
    /**
     * Assigna goleadors i assistències amb probabilitats realistes.
     */
    private void assignarGoleadoresYAsistencias(Equip equip, int gols, boolean esLocal) {
        if (gols == 0) return;
        
        List<Jugador> titulares = equip.getTactica().getTitulares();
        if (titulares.isEmpty()) return;
        
        for (int i = 0; i < gols; i++) {
            // Seleccionar goleador segons posició i qualitat
            Jugador goleador = seleccionarGoleador(titulares);
            int minut = 1 + random.nextInt(90);
            
            // Registrar gol
            goleador.registrarGol();
            goleadores.put(goleador, goleadores.getOrDefault(goleador, 0) + 1);
            
            // 70% probabilitat d'assistència
            Jugador assistent = null;
            if (random.nextDouble() < 0.70) {
                assistent = seleccionarAssistent(titulares, goleador);
                if (assistent != null) {
                    assistent.registrarAssistencia();
                    asistentes.put(assistent, asistentes.getOrDefault(assistent, 0) + 1);
                }
            }
            
            // Crear esdeveniment
            String descripcion = assistent != null 
                ? String.format("Gol de %s (Assist: %s)", goleador.getNom(), assistent.getNom())
                : String.format("Gol de %s", goleador.getNom());
            
            EventoPartido evento = new EventoPartido(minut, EventoPartido.TipoEvento.GOL, 
                                                    goleador, descripcion);
            eventos.add(evento);
            
            // Afegir a cronologia (compatibilitat)
            if (assistent != null) {
                cronologia.add(String.format("  ⚽ %d' %s (assist: %s)", 
                                           minut, goleador.getNom(), assistent.getNom()));
            } else {
                cronologia.add(String.format("  ⚽ %d' %s", minut, goleador.getNom()));
            }
        }
    }
    
    /**
     * Selecciona un goleador basat en posició i qualitat efectiva.
     */
    private Jugador seleccionarGoleador(List<Jugador> jugadores) {
        double[] probabilitats = new double[jugadores.size()];
        double sumaProbabilitats = 0;
        
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            double prob = j.getQualitatEfectiva();
            
            // Ajustar per posició (DAV més probable)
            switch (j.getPosicio()) {
                case "DAV": prob *= 2.0; break;
                case "MIG": prob *= 1.2; break;
                case "DEF": prob *= 0.3; break;
                case "POR": prob *= 0.05; break;
            }
            
            probabilitats[i] = prob;
            sumaProbabilitats += prob;
        }
        
        if (sumaProbabilitats == 0) {
            return jugadores.get(random.nextInt(jugadores.size()));
        }
        
        // Seleccionar aleatòriament basat en probabilitats
        double rand = random.nextDouble() * sumaProbabilitats;
        double acumulat = 0;
        
        for (int i = 0; i < jugadores.size(); i++) {
            acumulat += probabilitats[i];
            if (rand <= acumulat) {
                return jugadores.get(i);
            }
        }
        
        return jugadores.get(jugadores.size() - 1);
    }
    
    /**
     * Selecciona un assistent (no pot ser el goleador ni porter).
     */
    private Jugador seleccionarAssistent(List<Jugador> jugadores, Jugador goleador) {
        List<Jugador> possibles = new ArrayList<>();
        
        for (Jugador j : jugadores) {
            if (!j.equals(goleador) && !j.getPosicio().equals("POR")) {
                possibles.add(j);
            }
        }
        
        if (possibles.isEmpty()) return null;
        
        // Probabilitat ponderada per qualitat
        return seleccionarGoleador(possibles);
    }
    
    /**
     * Simula targetes (amarilles 5%, vermelles 1% per jugador).
     */
    private void simularTarjetas(Equip equip) {
        for (Jugador j : equip.getTactica().getTitulares()) {
            // Targeta vermella (1%)
            if (random.nextDouble() < 0.01) {
                j.registrarTargetaVermella();
                tarjetasRojas.put(j, tarjetasRojas.getOrDefault(j, 0) + 1);
                
                int minut = 1 + random.nextInt(90);
                EventoPartido evento = new EventoPartido(minut, EventoPartido.TipoEvento.TARJETA_ROJA,
                                                        j, "Targeta vermella per " + j.getNom());
                eventos.add(evento);
                
            } else if (random.nextDouble() < 0.05) {
                // Targeta groga (5%)
                j.registrarTargetaGroga();
                tarjetasAmarillas.put(j, tarjetasAmarillas.getOrDefault(j, 0) + 1);
                
                int minut = 1 + random.nextInt(90);
                EventoPartido evento = new EventoPartido(minut, EventoPartido.TipoEvento.TARJETA_AMARILLA,
                                                        j, "Targeta groga per " + j.getNom());
                eventos.add(evento);
            }
        }
    }
    
    /**
     * Calcula el rating d'un equip segons el resultat.
     */
    private double calcularRatingEquipo(int golsAFavor, int golsEnContra) {
        double rating = 5.0;
        rating += golsAFavor * 2.0;
        rating -= golsEnContra * 1.5;
        return Math.max(0.0, Math.min(10.0, rating));
    }
    
    /**
     * Registra totes les estadístiques als objectes EstadisticasJugador.
     */
    private void registrarEstadisticasEnJugadores() {
        // Gols
        for (Map.Entry<Jugador, Integer> entry : goleadores.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                entry.getKey().getEstadisticas().registrarGol(jornada);
            }
        }
        
        // Assistències
        for (Map.Entry<Jugador, Integer> entry : asistentes.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                entry.getKey().getEstadisticas().registrarAsistencia();
            }
        }
        
        // Targetes grogues
        for (Map.Entry<Jugador, Integer> entry : tarjetasAmarillas.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                entry.getKey().getEstadisticas().registrarTarjetaAmarilla();
            }
        }
        
        // Targetes vermelles
        for (Map.Entry<Jugador, Integer> entry : tarjetasRojas.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                entry.getKey().getEstadisticas().registrarTarjetaRoja();
            }
        }
        
        // Actualitzar ratings de tots els jugadors
        for (Jugador j : equip1.getTactica().getTitulares()) {
            double rating = j.getEstadisticas().calcularRatingPartido();
            j.getEstadisticas().actualizarRating(rating);
        }
        for (Jugador j : equip2.getTactica().getTitulares()) {
            double rating = j.getEstadisticas().calcularRatingPartido();
            j.getEstadisticas().actualizarRating(rating);
        }
    }
    
    /**
     * Actualitza la forma dels jugadors després del partit.
     */
    private void actualitzarFormaDespresPartit() {
        boolean victoria1 = golsEquip1 > golsEquip2;
        boolean victoria2 = golsEquip2 > golsEquip1;
        boolean empat = golsEquip1 == golsEquip2;
        
        for (Jugador j : equip1.getTactica().getTitulares()) {
            j.actualitzarFormaDespresPartit(victoria1, empat);
        }
        
        for (Jugador j : equip2.getTactica().getTitulares()) {
            j.actualitzarFormaDespresPartit(victoria2, empat);
        }
    }
    
    /**
     * Genera un resum detallat del partit amb tots els esdeveniments.
     */
    public String generarResumenDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append(String.format("║     🏟️  JORNADA %d - RESUM DEL PARTIT          ║\n", jornada));
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  %s  %d - %d  %s%n", 
                               truncar(equip1.getNom(), 15), golsEquip1, 
                               golsEquip2, truncar(equip2.getNom(), 15)));
        sb.append(String.format("║  Rating: %.1f                   Rating: %.1f ║\n", 
                               ratingEquip1, ratingEquip2));
        sb.append("╠════════════════════════════════════════════════╣\n");
        
        // Esdeveniments
        if (!eventos.isEmpty()) {
            sb.append("║  ESDEVENIMENTS DEL PARTIT:\n");
            for (EventoPartido evt : eventos) {
                sb.append(String.format("║  %s\n", evt.toString()));
            }
            sb.append("╠════════════════════════════════════════════════╣\n");
        }
        
        // Golejadors
        if (!goleadores.isEmpty()) {
            sb.append("║  ⚽ GOLEJADORS:\n");
            for (Map.Entry<Jugador, Integer> entry : goleadores.entrySet()) {
                sb.append(String.format("║    %s (#%d): %d gol(s)\n", 
                                      entry.getKey().getNom(), 
                                      entry.getKey().getDorsal(),
                                      entry.getValue()));
            }
        }
        
        sb.append("╚════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    private String truncar(String text, int maxLen) {
        if (text.length() <= maxLen) {
            return String.format("%-" + maxLen + "s", text);
        }
        return text.substring(0, maxLen);
    }

    // Getters
    
    public Equip getEquip1() { return equip1; }
    public Equip getEquip2() { return equip2; }
    public int getGolsEquip1() { return golsEquip1; }
    public int getGolsEquip2() { return golsEquip2; }
    public int getJornada() { return jornada; }
    public boolean isFinalizado() { return finalizado; }
    public LocalDateTime getFechaPartido() { return fechaPartido; }
    public List<EventoPartido> getEventos() { return new ArrayList<>(eventos); }
    public Map<Jugador, Integer> getGoleadores() { return new HashMap<>(goleadores); }
    public Map<Jugador, Integer> getAsistentes() { return new HashMap<>(asistentes); }
    public double getRatingEquip1() { return ratingEquip1; }
    public double getRatingEquip2() { return ratingEquip2; }
    public List<String> getCronologia() { return new ArrayList<>(cronologia); }
    
    public Equip getGuanyador() {
        if (golsEquip1 > golsEquip2) return equip1;
        if (golsEquip2 > golsEquip1) return equip2;
        return null; // Empat
    }
    
    @Override
    public String toString() {
        return generarResumenDetallado();
    }
    
    public String getResultatSimple() {
        return String.format("%s %d - %d %s", 
                equip1.getNom(), golsEquip1, golsEquip2, equip2.getNom());
    }
}
