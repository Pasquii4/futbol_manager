package com.politecnics.football.engine.model;

import java.util.*;

public class TacticaEquip {
    private Formacion formacion;
    private EstiloJoc estiloJoc;
    private int intensidadPresion; // 1-10
    private Map<String, List<Jugador>> jugadoresPorPosicion;
    private Jugador portero;
    
    public TacticaEquip() {
        this(Formacion.F_4_4_2, EstiloJoc.EQUILIBRADO, 5);
    }
    
    public TacticaEquip(Formacion formacion, EstiloJoc estiloJoc, int intensidadPresion) {
        this.formacion = formacion;
        this.estiloJoc = estiloJoc;
        this.intensidadPresion = Math.max(1, Math.min(10, intensidadPresion));
        this.jugadoresPorPosicion = new HashMap<>();
        inicializarMapa();
    }
    
    private void inicializarMapa() {
        jugadoresPorPosicion.put("POR", new ArrayList<>());
        jugadoresPorPosicion.put("DEF", new ArrayList<>());
        jugadoresPorPosicion.put("MIG", new ArrayList<>());
        jugadoresPorPosicion.put("DAV", new ArrayList<>());
    }
    
    public boolean generarAlineacionAutomatica(List<Jugador> jugadores) {
        inicializarMapa();
        portero = null;
        
        List<Jugador> disponibles = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.estaDisponible() && j.getFatiga() < 90.0) {
                disponibles.add(j);
            }
        }
        
        Map<String, List<Jugador>> porPosicion = new HashMap<>();
        porPosicion.put("POR", new ArrayList<>());
        porPosicion.put("DEF", new ArrayList<>());
        porPosicion.put("MIG", new ArrayList<>());
        porPosicion.put("DAV", new ArrayList<>());
        
        for (Jugador j : disponibles) {
            String pos = j.getPosicio();
            if (porPosicion.containsKey(pos)) {
                porPosicion.get(pos).add(j);
            }
        }
        
        for (List<Jugador> lista : porPosicion.values()) {
            lista.sort((j1, j2) -> Double.compare(j2.getQualitatEfectiva(), j1.getQualitatEfectiva()));
        }
        
        if (porPosicion.get("POR").isEmpty()) return false;
        portero = porPosicion.get("POR").get(0);
        jugadoresPorPosicion.get("POR").add(portero);
        
        if (!llenarPosicion("DEF", formacion.getDefensas(), porPosicion)) return false;
        if (!llenarPosicion("MIG", formacion.getMediocampistas(), porPosicion)) return false;
        if (!llenarPosicion("DAV", formacion.getDelanteros(), porPosicion)) return false;
        
        return true;
    }
    
    private boolean llenarPosicion(String pos, int necesarios, Map<String, List<Jugador>> pool) {
        if (pool.get(pos).size() < necesarios) return false;
        for (int i = 0; i < necesarios; i++) {
            jugadoresPorPosicion.get(pos).add(pool.get(pos).get(i));
        }
        return true;
    }
    
    public double calcularFuerzaTotal(boolean esLocal) {
        double qualitatPortero = portero != null ? portero.getQualitatEfectiva() : 0;
        double qualitatDefensa = calcularQualitatLinea("DEF");
        double qualitatMitjcamp = calcularQualitatLinea("MIG");
        double qualitatAtac = calcularQualitatLinea("DAV");
        
        double fuerzaBase = (qualitatPortero * 0.15 + 
                            qualitatDefensa * 0.25 + 
                            qualitatMitjcamp * 0.30 + 
                            qualitatAtac * 0.30);
        
        double bonusFormacion = (formacion.getBonusAtaque() + formacion.getBonusDefensa()) / 2.0;
        double multiplicadorEstilo = (estiloJoc.getMultiplicadorAtaque() + estiloJoc.getMultiplicadorDefensa()) / 2.0;
        double bonusPresion = 1.0 + (intensidadPresion * 0.02);
        double ventajaCasa = esLocal ? 1.10 : 1.0;
        
        return fuerzaBase * bonusFormacion * multiplicadorEstilo * bonusPresion * ventajaCasa;
    }
    
    private double calcularQualitatLinea(String posicion) {
        List<Jugador> jugadores = jugadoresPorPosicion.get(posicion);
        if (jugadores == null || jugadores.isEmpty()) return 0.0;
        
        double suma = 0.0;
        for (Jugador j : jugadores) {
            suma += j.getQualitatEfectiva();
        }
        return suma / jugadores.size();
    }
    
    public void aplicarFatigaPostPartido() {
        if (portero != null) {
            portero.aumentarFatiga(5.0 + (intensidadPresion * 0.5));
        }
        for (String pos : Arrays.asList("DEF", "MIG", "DAV")) {
            for (Jugador j : jugadoresPorPosicion.get(pos)) {
                j.aumentarFatiga(8.0 + (intensidadPresion * 1.0));
            }
        }
    }
    
    public List<Jugador> getTitulares() {
        List<Jugador> titulares = new ArrayList<>();
        if (portero != null) titulares.add(portero);
        titulares.addAll(jugadoresPorPosicion.get("DEF"));
        titulares.addAll(jugadoresPorPosicion.get("MIG"));
        titulares.addAll(jugadoresPorPosicion.get("DAV"));
        return titulares;
    }

    // Getters and Setters simplified
    public Formacion getFormacion() { return formacion; }
    public void setFormacion(Formacion formacion) { this.formacion = formacion; }
}
