package model;

import java.util.ArrayList;
import java.util.Collections;
import comparators.ComparadorJugadorPosicio;

/**
 * Classe que representa un equip de futbol.
 * Gestiona jugadors, entrenador i dades de l'equip.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class Equip {
    private final String nom;
    private final int anyFundacio;
    private final String ciutat;
    private String estadi;
    private String president;
    private Entrenador entrenador;
    private ArrayList<Jugador> jugadors;
    private Presupuesto presupuesto;
    private TacticaEquip tactica;

    /**
     * Constructor amb camps obligatoris.
     * 
     * @param nom El nom de l'equip
     * @param anyFundacio L'any de fundació
     * @param ciutat La ciutat de l'equip
     */
    public Equip(String nom, int anyFundacio, String ciutat) {
        this.nom = nom;
        this.anyFundacio = anyFundacio;
        this.ciutat = ciutat;
        this.jugadors = new ArrayList<>();
        this.presupuesto = new Presupuesto(1000000.0, 10000.0); // Pressupost inicial per defecte
        this.tactica = new TacticaEquip(); // Tàctica per defecte
    }

    /**
     * Constructor amb tots els camps.
     * 
     * @param nom El nom de l'equip
     * @param anyFundacio L'any de fundació
     * @param ciutat La ciutat de l'equip
     * @param estadi El nom de l'estadi
     * @param president El nom del president
     */
    public Equip(String nom, int anyFundacio, String ciutat, String estadi, String president) {
        this(nom, anyFundacio, ciutat);
        this.estadi = estadi;
        this.president = president;
    }

    /**
     * Calcula la qualificació mitjana de l'equip basant-se en la qualitat dels jugadors.
     * 
     * @return La qualitat mitjana, o 0 si no hi ha jugadors
     */
    public double qualificacioMitjana() {
        if (jugadors.isEmpty()) {
            return 0.0;
        }
        
        double suma = 0.0;
        for (Jugador jugador : jugadors) {
            suma += jugador.getQualitat();
        }
        
        return suma / jugadors.size();
    }

    /**
     * Calcula la motivació mitjana de l'equip.
     * 
     * @return La motivació mitjana, o 5.0 si no hi ha jugadors
     */
    public double motivacioMitjana() {
        if (jugadors.isEmpty()) {
            return 5.0;
        }
        
        double suma = 0.0;
        for (Jugador jugador : jugadors) {
            suma += jugador.getMotivacio();
        }
        
        return suma / jugadors.size();
    }

    /**
     * Afegeix un jugador a l'equip.
     * 
     * @param jugador El jugador a afegir
     */
    public void afegirJugador(Jugador jugador) {
        jugadors.add(jugador);
    }

    /**
     * Elimina un jugador de l'equip.
     * 
     * @param jugador El jugador a eliminar
     * @return true si s'ha eliminat, false altrament
     */
    public boolean eliminarJugador(Jugador jugador) {
        return jugadors.remove(jugador);
    }

    /**
     * Busca un jugador per nom i dorsal.
     * 
     * @param nom El nom del jugador
     * @param dorsal El dorsal del jugador
     * @return El jugador si es troba, null altrament
     */
    public Jugador buscarJugador(String nom, int dorsal) {
        for (Jugador jugador : jugadors) {
            if (jugador.getNom().equalsIgnoreCase(nom) && jugador.getDorsal() == dorsal) {
                return jugador;
            }
        }
        return null;
    }

    /**
     * Comprova si un dorsal està disponible.
     * 
     * @param dorsal El dorsal a comprovar
     * @return true si està disponible, false si ja està en ús
     */
    public boolean dorsalDisponible(int dorsal) {
        for (Jugador jugador : jugadors) {
            if (jugador.getDorsal() == dorsal) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prepara l'equip per a un partit generant l'alineació automàtica.
     */
    public void prepararPartido() {
        if (tactica != null) {
            tactica.generarAlineacionAutomatica(jugadors);
        }
    }

    /**
     * Obté la llista de jugadors ordenada per posició i qualitat.
     * 
     * @return ArrayList de jugadors ordenats
     */
    public ArrayList<Jugador> getJugadorsOrdenats() {
        ArrayList<Jugador> jugadorsOrdenats = new ArrayList<>(jugadors);
        Collections.sort(jugadorsOrdenats, new ComparadorJugadorPosicio());
        return jugadorsOrdenats;
    }

    // Getters i setters

    /**
     * Obté el nom de l'equip.
     * 
     * @return El nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Obté l'any de fundació.
     * 
     * @return L'any de fundació
     */
    public int getAnyFundacio() {
        return anyFundacio;
    }

    /**
     * Obté la ciutat.
     * 
     * @return La ciutat
     */
    public String getCiutat() {
        return ciutat;
    }

    /**
     * Obté l'estadi.
     * 
     * @return L'estadi o null si no està definit
     */
    public String getEstadi() {
        return estadi;
    }

    /**
     * Estableix l'estadi.
     * 
     * @param estadi El nom de l'estadi
     */
    public void setEstadi(String estadi) {
        this.estadi = estadi;
    }

    /**
     * Obté el president.
     * 
     * @return El president o null si no està definit
     */
    public String getPresident() {
        return president;
    }

    /**
     * Estableix el president.
     * 
     * @param president El nom del president
     */
    public void setPresident(String president) {
        this.president = president;
    }

    /**
     * Obté l'entrenador.
     * 
     * @return L'entrenador o null si no n'hi ha
     */
    public Entrenador getEntrenador() {
        return entrenador;
    }

    /**
     * Estableix l'entrenador.
     * 
     * @param entrenador L'entrenador
     */
    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    /**
     * Obté la llista de jugadors.
     * 
     * @return ArrayList de jugadors
     */
    public ArrayList<Jugador> getJugadors() {
        return jugadors;
    }

    /**
     * Obté el pressupost de l'equip.
     * 
     * @return El pressupost
     */
    public Presupuesto getPresupuesto() {
        return presupuesto;
    }

    /**
     * Estableix el pressupost de l'equip.
     * 
     * @param presupuesto El nou pressupost
     */
    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    /**
     * Obté la tàctica de l'equip.
     * 
     * @return La tàctica
     */
    public TacticaEquip getTactica() {
        return tactica;
    }

    /**
     * Estableix la tàctica de l'equip.
     * 
     * @param tactica La nova tàctica
     */
    public void setTactica(TacticaEquip tactica) {
        this.tactica = tactica;
    }

    /**
     * Retorna una representació detallada de l'equip.
     * 
     * @return String amb totes les dades de l'equip
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n╔════════════════════════════════════════════════════════════════╗\n"));
        sb.append(String.format("║  🏆 %s (Fundat: %d)\n", nom, anyFundacio));
        sb.append(String.format("╠════════════════════════════════════════════════════════════════╣\n"));
        sb.append(String.format("║  📍 Ciutat: %s\n", ciutat));
        
        if (estadi != null && !estadi.isEmpty()) {
            sb.append(String.format("║  🏟️  Estadi: %s\n", estadi));
        }
        if (president != null && !president.isEmpty()) {
            sb.append(String.format("║  👤 President/a: %s\n", president));
        }
        
        sb.append(String.format("║  ⭐ Qualitat Mitjana: %.2f\n", qualificacioMitjana()));
        sb.append(String.format("╠════════════════════════════════════════════════════════════════╣\n"));
        
        if (entrenador != null) {
            sb.append(String.format("║  %s\n", entrenador.toString()));
        } else {
            sb.append("║  ❌ Sense entrenador\n");
        }
        
        sb.append(String.format("╠════════════════════════════════════════════════════════════════╣\n"));
        sb.append(String.format("║  👥 PLANTILLA (%d jugadors):\n", jugadors.size()));
        
        ArrayList<Jugador> jugadorsOrdenats = getJugadorsOrdenats();
        for (Jugador jugador : jugadorsOrdenats) {
            sb.append(String.format("║  %s\n", jugador.toString()));
        }
        
        sb.append(String.format("╚════════════════════════════════════════════════════════════════╝\n"));
        return sb.toString();
    }
}
