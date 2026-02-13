package model;

/**
 * Enum que representa els diferents estils de joc disponibles.
 * Cada estil té multiplicadors que afecten l'atac i la defensa.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public enum EstiloJoc {
    ULTRA_OFENSIVO("Ultra Ofensiu", 1.30, 0.70),
    OFENSIVO("Ofensiu", 1.15, 0.85),
    EQUILIBRADO("Equilibrat", 1.0, 1.0),
    DEFENSIVO("Defensiu", 0.85, 1.15),
    ULTRA_DEFENSIVO("Ultra Defensiu", 0.70, 1.30);
    
    private final String nombre;
    private final double multiplicadorAtaque;
    private final double multiplicadorDefensa;
    
    /**
     * Constructor de l'estil de joc.
     * 
     * @param nombre Nom de l'estil
     * @param multiplicadorAtaque Multiplicador d'atac
     * @param multiplicadorDefensa Multiplicador de defensa
     */
    EstiloJoc(String nombre, double multiplicadorAtaque, double multiplicadorDefensa) {
        this.nombre = nombre;
        this.multiplicadorAtaque = multiplicadorAtaque;
        this.multiplicadorDefensa = multiplicadorDefensa;
    }
    
    /**
     * Obté el multiplicador d'atac.
     * 
     * @return Multiplicador d'atac (0.70 - 1.30)
     */
    public double getMultiplicadorAtaque() {
        return multiplicadorAtaque;
    }
    
    /**
     * Obté el multiplicador de defensa.
     * 
     * @return Multiplicador de defensa (0.70 - 1.30)
     */
    public double getMultiplicadorDefensa() {
        return multiplicadorDefensa;
    }
    
    /**
     * Obté el nom de l'estil.
     * 
     * @return Nom de l'estil
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Descripció detallada de l'estil.
     * 
     * @return String amb la descripció
     */
    public String getDescripcion() {
        if (multiplicadorAtaque > 1.1) {
            return "Estil molt agressiu, prioritza l'atac sobre la defensa";
        } else if (multiplicadorAtaque > 1.0) {
            return "Estil ofensiu, busca el gol constantment";
        } else if (multiplicadorDefensa > 1.1) {
            return "Estil molt defensiu, prioritza no encaixar gols";
        } else if (multiplicadorDefensa > 1.0) {
            return "Estil defensiu, joc conservador";
        } else {
            return "Estil equilibrat, balanç entre atac i defensa";
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (Atac: %.2f, Defensa: %.2f)", 
                           nombre, multiplicadorAtaque, multiplicadorDefensa);
    }
}
