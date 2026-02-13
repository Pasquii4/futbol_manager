package model;

/**
 * Classe que representa una lesió d'un jugador.
 * Controla el tipus, severitat i temps de recuperació.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class Lesion {
    private String tipo;
    private int jornadesRestants;
    private double severidad;
    
    /**
     * Constructor de la lesió.
     * 
     * @param tipo Tipus de lesió (MUSCULAR, OSEA, ARTICULAR, LEVE)
     * @param jornades Jornades necessàries per recuperar-se
     * @param severidad Gravetat de la lesió (1-10)
     */
    public Lesion(String tipo, int jornades, double severidad) {
        this.tipo = tipo;
        this.jornadesRestants = jornades;
        this.severidad = Math.max(1.0, Math.min(10.0, severidad));
    }
    
    /**
     * Avança el procés de recuperació en una jornada.
     */
    public void avanzarRecuperacion() {
        if (jornadesRestants > 0) {
            jornadesRestants--;
        }
    }
    
    /**
     * Comprova si el jugador ja està recuperat.
     * 
     * @return true si ja no queden jornades de recuperació
     */
    public boolean estaRecuperado() {
        return jornadesRestants <= 0;
    }
    
    /**
     * Obté una descripció de la lesió segons la severitat.
     * 
     * @return Descripció textual
     */
    public String getDescripcionSeveridad() {
        if (severidad >= 8.0) {
            return "Molt greu";
        } else if (severidad >= 6.0) {
            return "Greu";
        } else if (severidad >= 4.0) {
            return "Moderada";
        } else {
            return "Lleu";
        }
    }
    
    // Getters
    
    public String getTipo() {
        return tipo;
    }
    
    public int getJornadesRestants() {
        return jornadesRestants;
    }
    
    public double getSeveridad() {
        return severidad;
    }
    
    @Override
    public String toString() {
        return String.format("Lesió %s (%s) - %d jornades restants", 
                           tipo, getDescripcionSeveridad(), jornadesRestants);
    }
}
