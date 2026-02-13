package model;

/**
 * Enum que representa les diferents formacions tàctiques disponibles.
 * Cada formació té una distribució específica de jugadors per línia.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public enum Formacion {
    F_4_4_2("4-4-2", 4, 4, 2),
    F_4_3_3("4-3-3", 4, 3, 3),
    F_3_5_2("3-5-2", 3, 5, 2),
    F_4_5_1("4-5-1", 4, 5, 1),
    F_5_3_2("5-3-2", 5, 3, 2),
    F_3_4_3("3-4-3", 3, 4, 3);
    
    private final String nombre;
    private final int defensas;
    private final int mediocampistas;
    private final int delanteros;
    
    /**
     * Constructor de la formació.
     * 
     * @param nombre Nom de la formació
     * @param defensas Nombre de defenses
     * @param mediocampistas Nombre de migcampistes
     * @param delanteros Nombre de davanters
     */
    Formacion(String nombre, int defensas, int mediocampistas, int delanteros) {
        this.nombre = nombre;
        this.defensas = defensas;
        this.mediocampistas = mediocampistas;
        this.delanteros = delanteros;
    }
    
    /**
     * Calcula el bonus d'atac basat en el nombre de davanters.
     * Fórmula: 1.0 + (davanters × 0.05)
     * 
     * @return Multiplicador d'atac
     */
    public double getBonusAtaque() {
        return 1.0 + (delanteros * 0.05);
    }
    
    /**
     * Calcula el bonus de defensa basat en el nombre de defenses.
     * Fórmula: 1.0 + (defenses × 0.05)
     * 
     * @return Multiplicador de defensa
     */
    public double getBonusDefensa() {
        return 1.0 + (defensas * 0.05);
    }
    
    /**
     * Determina quants jugadors d'una posició específica necessita la formació.
     * 
     * @param posicion Posició del jugador (POR, DEF, MIG, DAV)
     * @return Nombre de jugadors necessaris d'aquesta posició
     */
    public int necesitaPosicion(String posicion) {
        switch (posicion.toUpperCase()) {
            case "POR":
                return 1;
            case "DEF":
                return defensas;
            case "MIG":
                return mediocampistas;
            case "DAV":
                return delanteros;
            default:
                return 0;
        }
    }
    
    /**
     * Obté el nombre total de jugadors titulars (sempre 11).
     * 
     * @return 11
     */
    public int getTotalJugadores() {
        return 1 + defensas + mediocampistas + delanteros; // 1 porter + resta
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getDefensas() {
        return defensas;
    }
    
    public int getMediocampistas() {
        return mediocampistas;
    }
    
    public int getDelanteros() {
        return delanteros;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
