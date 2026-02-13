package model;

/**
 * Excepció personalitzada per indicar que no hi ha suficient saldo.
 * S'utilitza en operacions financeres quan es vol gastar més del saldo disponible.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class SaldoInsuficienteException extends Exception {
    
    /**
     * Constructor amb missatge personalitzat.
     * 
     * @param message Missatge descriptiu de l'error
     */
    public SaldoInsuficienteException(String message) {
        super(message);
    }
    
    /**
     * Constructor amb missatge i causa.
     * 
     * @param message Missatge descriptiu de l'error
     * @param cause Causa original de l'excepció
     */
    public SaldoInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
