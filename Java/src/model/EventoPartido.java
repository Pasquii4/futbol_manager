package model;

/**
 * Classe que representa un esdeveniment dins d'un partit.
 * Pot ser un gol, assistència, targeta o lesió.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class EventoPartido {
    
    /**
     * Enum amb els tipus d'esdeveniments possibles.
     */
    public enum TipoEvento {
        GOL("⚽"),
        ASISTENCIA("🎯"),
        TARJETA_AMARILLA("🟨"),
        TARJETA_ROJA("🟥"),
        LESION("🚑");
        
        private final String emoji;
        
        TipoEvento(String emoji) {
            this.emoji = emoji;
        }
        
        public String getEmoji() {
            return emoji;
        }
    }
    
    private int minuto;
    private TipoEvento tipo;
    private Jugador jugadorInvolucrado;
    private String descripcion;
    
    /**
     * Constructor complet.
     * 
     * @param minuto Minut del partit (1-90)
     * @param tipo Tipus d'esdeveniment
     * @param jugadorInvolucrado Jugador que protagonitza l'esdeveniment
     * @param descripcion Descripció de l'esdeveniment
     */
    public EventoPartido(int minuto, TipoEvento tipo, Jugador jugadorInvolucrado, String descripcion) {
        this.minuto = Math.max(1, Math.min(90, minuto));
        this.tipo = tipo;
        this.jugadorInvolucrado = jugadorInvolucrado;
        this.descripcion = descripcion;
    }
    
    /**
     * Constructor simple sense descripció.
     */
    public EventoPartido(int minuto, TipoEvento tipo, Jugador jugadorInvolucrado) {
        this(minuto, tipo, jugadorInvolucrado, generarDescripcionAutomatica(tipo, jugadorInvolucrado));
    }
    
    /**
     * Genera una descripció automàtica segons el tipus d'esdeveniment.
     */
    private static String generarDescripcionAutomatica(TipoEvento tipo, Jugador jugador) {
        switch (tipo) {
            case GOL:
                return "Gol de " + jugador.getNom();
            case ASISTENCIA:
                return "Assistència de " + jugador.getNom();
            case TARJETA_AMARILLA:
                return "Targeta groga per " + jugador.getNom();
            case TARJETA_ROJA:
                return "Targeta vermella per " + jugador.getNom();
            case LESION:
                return "Lesió de " + jugador.getNom();
            default:
                return "";
        }
    }
    
    // Getters
    
    public int getMinuto() {
        return minuto;
    }
    
    public TipoEvento getTipo() {
        return tipo;
    }
    
    public Jugador getJugadorInvolucrado() {
        return jugadorInvolucrado;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return String.format("%s Min %d' - %s", 
                           tipo.getEmoji(), minuto, descripcion);
    }
}
