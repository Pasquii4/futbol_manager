package model;

import java.util.*;

/**
 * Classe que gestiona la tàctica d'un equip.
 * Inclou formació, estil de joc, intensitat de pressió i alineació.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class TacticaEquip {
    private Formacion formacion;
    private EstiloJoc estiloJoc;
    private int intensidadPresion; // 1-10
    private Map<String, List<Jugador>> jugadoresPorPosicion;
    private Jugador portero;
    
    /**
     * Constructor amb valors per defecte.
     */
    public TacticaEquip() {
        this.formacion = Formacion.F_4_4_2; // Formació clàssica per defecte
        this.estiloJoc = EstiloJoc.EQUILIBRADO;
        this.intensidadPresion = 5; // Mitjana
        this.jugadoresPorPosicion = new HashMap<>();
        inicializarMapa();
    }
    
    /**
     * Constructor complet.
     */
    public TacticaEquip(Formacion formacion, EstiloJoc estiloJoc, int intensidadPresion) {
        this.formacion = formacion;
        this.estiloJoc = estiloJoc;
        this.intensidadPresion = Math.max(1, Math.min(10, intensidadPresion));
        this.jugadoresPorPosicion = new HashMap<>();
        inicializarMapa();
    }
    
    /**
     * Inicialitza el mapa de jugadors per posició.
     */
    private void inicializarMapa() {
        jugadoresPorPosicion.put("POR", new ArrayList<>());
        jugadoresPorPosicion.put("DEF", new ArrayList<>());
        jugadoresPorPosicion.put("MIG", new ArrayList<>());
        jugadoresPorPosicion.put("DAV", new ArrayList<>());
    }
    
    /**
     * Genera automàticament l'alineació segons la formació i disponibilitat de jugadors.
     * Filtra jugadors lesionats i amb fatiga >= 90%.
     * 
     * @param jugadores Llista completa de jugadors de l'equip
     * @return true si s'ha pogut generar alineació completa (11 jugadors)
     */
    public boolean generarAlineacionAutomatica(List<Jugador> jugadores) {
        // Netejar alineació actual
        inicializarMapa();
        portero = null;
        
        // Filtrar jugadors disponibles
        List<Jugador> disponibles = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.estaDisponible() && j.getFatiga() < 90.0) {
                disponibles.add(j);
            }
        }
        
        // Separar per posicions
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
        
        // Ordenar cada posició per qualitat efectiva (descendent)
        for (List<Jugador> lista : porPosicion.values()) {
            lista.sort((j1, j2) -> Double.compare(j2.getQualitatEfectiva(), j1.getQualitatEfectiva()));
        }
        
        // Seleccionar porter (1)
        if (porPosicion.get("POR").isEmpty()) {
            return false; // No hi ha porter disponible
        }
        portero = porPosicion.get("POR").get(0);
        jugadoresPorPosicion.get("POR").add(portero);
        
        // Seleccionar defenses
        int defensasNecesarias = formacion.getDefensas();
        if (porPosicion.get("DEF").size() < defensasNecesarias) {
            return false; // No hi ha prou defenses
        }
        for (int i = 0; i < defensasNecesarias; i++) {
            jugadoresPorPosicion.get("DEF").add(porPosicion.get("DEF").get(i));
        }
        
        // Seleccionar migcampistes
        int mediosNecesarios = formacion.getMediocampistas();
        if (porPosicion.get("MIG").size() < mediosNecesarios) {
            return false; // No hi ha prou migcampistes
        }
        for (int i = 0; i < mediosNecesarios; i++) {
            jugadoresPorPosicion.get("MIG").add(porPosicion.get("MIG").get(i));
        }
        
        // Seleccionar davanters
        int delanterosNecesarios = formacion.getDelanteros();
        if (porPosicion.get("DAV").size() < delanterosNecesarios) {
            return false; // No hi ha prou davanters
        }
        for (int i = 0; i < delanterosNecesarios; i++) {
            jugadoresPorPosicion.get("DAV").add(porPosicion.get("DAV").get(i));
        }
        
        return true; // Alineació completa
    }
    
    /**
     * Calcula la força total de l'equip amb tots els multiplicadors.
     * 
     * @param esLocal true si l'equip juga a casa
     * @return Força total calculada
     */
    public double calcularFuerzaTotal(boolean esLocal) {
        // Calcular qualitat mitjana de cada línia
        double qualitatPortero = portero != null ? portero.getQualitatEfectiva() : 0;
        double qualitatDefensa = calcularQualitatLinea("DEF");
        double qualitatMitjcamp = calcularQualitatLinea("MIG");
        double qualitatAtac = calcularQualitatLinea("DAV");
        
        // Força base (combinació de totes les línies)
        double fuerzaBase = (qualitatPortero * 0.15 + 
                            qualitatDefensa * 0.25 + 
                            qualitatMitjcamp * 0.30 + 
                            qualitatAtac * 0.30);
        
        // Aplicar bonus de formació
        double bonusFormacion = (formacion.getBonusAtaque() + formacion.getBonusDefensa()) / 2.0;
        
        // Aplicar multiplicadors d'estil
        double multiplicadorEstilo = (estiloJoc.getMultiplicadorAtaque() + 
                                     estiloJoc.getMultiplicadorDefensa()) / 2.0;
        
        // Bonus de pressió (cada punt suma 2%)
        double bonusPresion = 1.0 + (intensidadPresion * 0.02);
        
        // Avantatge de casa (10% si és local)
        double ventajaCasa = esLocal ? 1.10 : 1.0;
        
        // Força total
        return fuerzaBase * bonusFormacion * multiplicadorEstilo * bonusPresion * ventajaCasa;
    }
    
    /**
     * Calcula la qualitat mitjana d'una línia.
     */
    private double calcularQualitatLinea(String posicion) {
        List<Jugador> jugadores = jugadoresPorPosicion.get(posicion);
        if (jugadores == null || jugadores.isEmpty()) {
            return 0.0;
        }
        
        double suma = 0.0;
        for (Jugador j : jugadores) {
            suma += j.getQualitatEfectiva();
        }
        return suma / jugadores.size();
    }
    
    /**
     * Aplica fatiga als jugadors titulars després d'un partit.
     */
    public void aplicarFatigaPostPartido() {
        // Fatiga del porter
        if (portero != null) {
            double fatigaPortero = 5.0 + (intensidadPresion * 0.5);
            portero.aumentarFatiga(fatigaPortero);
        }
        
        // Fatiga dels jugadors de camp
        for (String pos : Arrays.asList("DEF", "MIG", "DAV")) {
            for (Jugador j : jugadoresPorPosicion.get(pos)) {
                double fatiga = 8.0 + (intensidadPresion * 1.0);
                j.aumentarFatiga(fatiga);
            }
        }
    }
    
    /**
     * Obté la llista de tots els titulars (11 jugadors).
     * 
     * @return Llista amb els 11 titulars
     */
    public List<Jugador> getTitulares() {
        List<Jugador> titulares = new ArrayList<>();
        if (portero != null) {
            titulares.add(portero);
        }
        for (String pos : Arrays.asList("DEF", "MIG", "DAV")) {
            titulares.addAll(jugadoresPorPosicion.get(pos));
        }
        return titulares;
    }
    
    /**
     * Visualitza la formació en format ASCII.
     * 
     * @return String amb la representació visual
     */
    public String visualizarFormacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║           ⚽ FORMACIÓ: ").append(formacion.getNombre()).append("                 ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        
        // Davanters
        sb.append("║ DAV: ");
        List<Jugador> davs = jugadoresPorPosicion.get("DAV");
        if (davs != null && !davs.isEmpty()) {
            for (Jugador j : davs) {
                sb.append(j.getDorsal()).append(" ");
            }
        }
        sb.append("\n");
        
        // Migcampistes
        sb.append("║ MIG: ");
        List<Jugador> migs = jugadoresPorPosicion.get("MIG");
        if (migs != null && !migs.isEmpty()) {
            for (Jugador j : migs) {
                sb.append(j.getDorsal()).append(" ");
            }
        }
        sb.append("\n");
        
        // Defenses
        sb.append("║ DEF: ");
        List<Jugador> defs = jugadoresPorPosicion.get("DEF");
        if (defs != null && !defs.isEmpty()) {
            for (Jugador j : defs) {
                sb.append(j.getDorsal()).append(" ");
            }
        }
        sb.append("\n");
        
        // Porter
        sb.append("║ POR: ");
        if (portero != null) {
            sb.append(portero.getDorsal());
        }
        sb.append("\n");
        
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append("║ Estil: ").append(estiloJoc.getNombre()).append("\n");
        sb.append("║ Intensitat pressió: ").append(intensidadPresion).append("/10\n");
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    // Getters i Setters
    
    public Formacion getFormacion() {
        return formacion;
    }
    
    public void setFormacion(Formacion formacion) {
        this.formacion = formacion;
    }
    
    public EstiloJoc getEstiloJoc() {
        return estiloJoc;
    }
    
    public void setEstiloJoc(EstiloJoc estiloJoc) {
        this.estiloJoc = estiloJoc;
    }
    
    public int getIntensidadPresion() {
        return intensidadPresion;
    }
    
    public void setIntensidadPresion(int intensidadPresion) {
        this.intensidadPresion = Math.max(1, Math.min(10, intensidadPresion));
    }
    
    public Jugador getPortero() {
        return portero;
    }
    
    public Map<String, List<Jugador>> getJugadoresPorPosicion() {
        return jugadoresPorPosicion;
    }
}
