package model;

/**
 * Classe que gestiona el pressupost d'un equip de futbol.
 * Controla ingressos, despeses, salaris i ingressos per partits.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class Presupuesto {
    private double saldo;
    private double saldoInicial;
    private double ingresosPorPartido;
    private double gastosMantenimiento;
    
    /**
     * Constructor amb saldo inicial.
     * 
     * @param saldoInicial El saldo inicial del pressupost
     */
    public Presupuesto(double saldoInicial) {
        this(saldoInicial, 5000.0); // Ingressos per partit per defecte
    }
    
    /**
     * Constructor complet.
     * 
     * @param saldoInicial El saldo inicial
     * @param ingresosPorPartido Ingressos base per partit
     */
    public Presupuesto(double saldoInicial, double ingresosPorPartido) {
        this.saldoInicial = saldoInicial;
        this.saldo = saldoInicial;
        this.ingresosPorPartido = ingresosPorPartido;
        this.gastosMantenimiento = 0.0;
    }
    
    /**
     * Verifica si el pressupost pot permetre's una despesa.
     * 
     * @param cantidad La quantitat a comprovar
     * @return true si hi ha suficient saldo, false altrament
     */
    public boolean puedePermitirse(double cantidad) {
        return this.saldo >= cantidad;
    }
    
    /**
     * Gasta una quantitat del pressupost.
     * 
     * @param cantidad La quantitat a gastar
     * @throws SaldoInsuficienteException Si no hi ha suficient saldo
     */
    public void gastar(double cantidad) throws SaldoInsuficienteException {
        if (!puedePermitirse(cantidad)) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficient. Disponible: %.2f€, Necessari: %.2f€", 
                             saldo, cantidad)
            );
        }
        this.saldo -= cantidad;
    }
    
    /**
     * Ingressa una quantitat al pressupost.
     * 
     * @param cantidad La quantitat a ingressar
     */
    public void ingresar(double cantidad) {
        if (cantidad > 0) {
            this.saldo += cantidad;
        }
    }
    
    /**
     * Processa els ingressos d'una jornada basant-se en el resultat del partit.
     * 
     * @param victoria True si l'equip ha guanyat
     * @param casa True si el partit era a casa
     */
    public void procesarJornada(boolean victoria, boolean casa) {
        double ingresos = 0.0;
        
        if (casa) {
            if (victoria) {
                // Victoria a casa: ingressos × 1.5
                ingresos = ingresosPorPartido * 1.5;
            } else {
                // Comprova si va ser empat o derrota
                // Per determinar això, necessitem més info, però assumim derrota per ara
                // En la integració amb Partit, caldria passar més informació
                ingresos = ingresosPorPartido * 1.0; // Assumim empat per defecte
            }
        } else {
            // Fora de casa sempre és × 0.8
            ingresos = ingresosPorPartido * 0.8;
        }
        
        ingresar(ingresos);
    }
    
    /**
     * Processa els ingressos amb informació completa del resultat.
     * 
     * @param victoria True si va guanyar
     * @param empate True si va empatar
     * @param casa True si era a casa
     */
    public void procesarJornadaCompleta(boolean victoria, boolean empate, boolean casa) {
        double ingresos = 0.0;
        
        if (casa) {
            if (victoria) {
                ingresos = ingresosPorPartido * 1.5;
            } else if (empate) {
                ingresos = ingresosPorPartido * 1.0;
            } else {
                // Derrota
                ingresos = ingresosPorPartido * 0.7;
            }
        } else {
            // Fora de casa
            ingresos = ingresosPorPartido * 0.8;
        }
        
        ingresar(ingresos);
    }
    
    /**
     * Paga els salaris mensuals dels jugadors i entrenador.
     * 
     * @throws SaldoInsuficienteException Si no hi ha suficient saldo per pagar
     */
    public void pagarSalarios() throws SaldoInsuficienteException {
        if (gastosMantenimiento > 0) {
            gastar(gastosMantenimiento);
        }
    }
    
    /**
     * Calcula els salaris totals a partir d'un equip.
     * 
     * @param equip L'equip del qual calcular els salaris
     */
    public void calcularGastosMantenimiento(Equip equip) {
        double totalSalarios = 0.0;
        
        // Sumar salaris dels jugadors
        for (Jugador jugador : equip.getJugadors()) {
            totalSalarios += jugador.getSouAnual() / 12.0; // Salari mensual
        }
        
        // Sumar salari de l'entrenador si existeix
        if (equip.getEntrenador() != null) {
            totalSalarios += equip.getEntrenador().getSouAnual() / 12.0;
        }
        
        this.gastosMantenimiento = totalSalarios;
    }
    
    /**
     * Genera un informe financer formatat.
     * 
     * @return String amb l'estat financer
     */
    public String generarReporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║           📊 INFORME FINANCER                  ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Saldo actual:      %,15.2f€         ║\n", saldo));
        sb.append(String.format("║ Saldo inicial:     %,15.2f€         ║\n", saldoInicial));
        
        double diferencia = saldo - saldoInicial;
        String signo = diferencia >= 0 ? "+" : "";
        sb.append(String.format("║ Diferència:        %s%,15.2f€        ║\n", signo, diferencia));
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Ingressos/partit:  %,15.2f€         ║\n", ingresosPorPartido));
        sb.append(String.format("║ Salaris mensuals:  %,15.2f€         ║\n", gastosMantenimiento));
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    // Getters i Setters
    
    public double getSaldo() {
        return saldo;
    }
    
    public double getSaldoInicial() {
        return saldoInicial;
    }
    
    public double getIngresosPorPartido() {
        return ingresosPorPartido;
    }
    
    public void setIngresosPorPartido(double ingresosPorPartido) {
        this.ingresosPorPartido = ingresosPorPartido;
    }
    
    public double getGastosMantenimiento() {
        return gastosMantenimiento;
    }
    
    public void setGastosMantenimiento(double gastosMantenimiento) {
        this.gastosMantenimiento = gastosMantenimiento;
    }
    
    @Override
    public String toString() {
        return String.format("Pressupost: %.2f€ (Inicial: %.2f€)", saldo, saldoInicial);
    }
}
