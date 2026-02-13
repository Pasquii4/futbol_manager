package test.model;

import model.Presupuesto;
import model.SaldoInsuficienteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per a la classe Presupuesto.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class PresupuestoTest {
    
    private Presupuesto presupuesto;
    
    @BeforeEach
    public void setUp() {
        // Configurar abans de cada test
        presupuesto = new Presupuesto(100000.0, 5000.0);
    }
    
    @Test
    public void testPuedePermitirse() {
        assertTrue(presupuesto.puedePermitirse(50000.0), 
                  "Hauria de poder permetre's 50000€ amb saldo de 100000€");
        assertFalse(presupuesto.puedePermitirse(150000.0), 
                   "No hauria de poder permetre's 150000€ amb saldo de 100000€");
        assertTrue(presupuesto.puedePermitirse(100000.0), 
                  "Hauria de poder permetre's exactament el saldo disponible");
    }
    
    @Test
    public void testGastarConSaldoInsuficiente() {
        Presupuesto p = new Presupuesto(1000.0);
        
        assertThrows(SaldoInsuficienteException.class, () -> {
            p.gastar(2000.0);
        }, "Hauria de llançar SaldoInsuficienteException en gastar més del saldo");
    }

    @Test
    public void testGastarConSaldoSuficiente() {
        try {
            presupuesto.gastar(30000.0);
            assertEquals(70000.0, presupuesto.getSaldo(), 0.01, 
                        "El saldo hauria de ser 70000€ després de gastar 30000€");
        } catch (SaldoInsuficienteException e) {
            fail("No hauria de llançar excepció amb saldo suficient");
        }
    }
    
    @Test
    public void testIngresar() {
        presupuesto.ingresar(25000.0);
        assertEquals(125000.0, presupuesto.getSaldo(), 0.01, 
                    "El saldo hauria de ser 125000€ després d'ingressar 25000€");
    }
    
    @Test
    public void testIngresarCantidadNegativa() {
        double saldoInicial = presupuesto.getSaldo();
        presupuesto.ingresar(-1000.0);
        assertEquals(saldoInicial, presupuesto.getSaldo(), 0.01, 
                    "No hauria d'acceptar ingressos negatius");
    }
    
    @Test
    public void testIngresosPorVictoriaEnCasa() {
        Presupuesto p = new Presupuesto(10000.0, 5000.0);
        p.procesarJornadaCompleta(true, false, true); // victòria en casa
        
        // 10000 + (5000 × 1.5) = 17500
        assertEquals(17500.0, p.getSaldo(), 0.01, 
                    "Victoria a casa hauria de donar ingressos × 1.5");
    }
    
    @Test
    public void testIngresosPorEmpateEnCasa() {
        Presupuesto p = new Presupuesto(10000.0, 5000.0);
        p.procesarJornadaCompleta(false, true, true); // empat en casa
        
        // 10000 + (5000 × 1.0) = 15000
        assertEquals(15000.0, p.getSaldo(), 0.01, 
                    "Empat a casa hauria de donar ingressos × 1.0");
    }
    
    @Test
    public void testIngresosPorDerrotaEnCasa() {
        Presupuesto p = new Presupuesto(10000.0, 5000.0);
        p.procesarJornadaCompleta(false, false, true); // derrota en casa
        
        // 10000 + (5000 × 0.7) = 13500
        assertEquals(13500.0, p.getSaldo(), 0.01, 
                    "Derrota a casa hauria de donar ingressos × 0.7");
    }
    
    @Test
    public void testIngresosFueraDeCasa() {
        Presupuesto p = new Presupuesto(10000.0, 5000.0);
        p.procesarJornadaCompleta(true, false, false); // victoria fora de casa
        
        // 10000 + (5000 × 0.8) = 14000
        assertEquals(14000.0, p.getSaldo(), 0.01, 
                    "Partit fora de casa hauria de donar ingressos × 0.8");
    }
    
    @Test
    public void testPagarSalariosConSaldoSuficiente() {
        presupuesto.setGastosMantenimiento(20000.0);
        
        try {
            presupuesto.pagarSalarios();
            assertEquals(80000.0, presupuesto.getSaldo(), 0.01, 
                        "El saldo hauria de reduir-se després de pagar salaris");
        } catch (SaldoInsuficienteException e) {
            fail("No hauria de llançar excepció amb saldo suficient");
        }
    }
    
    @Test
    public void testPagarSalariosConSaldoInsuficiente() {
        Presupuesto p = new Presupuesto(5000.0);
        p.setGastosMantenimiento(10000.0);
        
        assertThrows(SaldoInsuficienteException.class, () -> {
            p.pagarSalarios();
        }, "Hauria de llançar excepció si no hi ha prou saldo per salaris");
    }
    
    @Test
    public void testGenerarReporte() {
        String reporte = presupuesto.generarReporte();
        
        assertNotNull(reporte, "El reporte no hauria de ser null");
        assertTrue(reporte.contains("INFORME FINANCER"), 
                  "El reporte hauria de contenir el títol");
        assertTrue(reporte.contains("100"), 
                  "El reporte hauria de contenir el saldo");
    }
    
    @Test
    public void testGettersAndSetters() {
        assertEquals(100000.0, presupuesto.getSaldo(), 0.01);
        assertEquals(100000.0, presupuesto.getSaldoInicial(), 0.01);
        assertEquals(5000.0, presupuesto.getIngresosPorPartido(), 0.01);
        
        presupuesto.setIngresosPorPartido(8000.0);
        assertEquals(8000.0, presupuesto.getIngresosPorPartido(), 0.01);
        
        presupuesto.setGastosMantenimiento(15000.0);
        assertEquals(15000.0, presupuesto.getGastosMantenimiento(), 0.01);
    }
    
    @Test
    public void testConstructorConUnParametro() {
        Presupuesto p = new Presupuesto(50000.0);
        
        assertEquals(50000.0, p.getSaldo(), 0.01);
        assertEquals(5000.0, p.getIngresosPorPartido(), 0.01, 
                    "Hauria de tenir ingressos per defecte de 5000€");
    }
    
    @Test  
    public void testMultiplesOperaciones() {
        try {
            // Ingressar
            presupuesto.ingresar(20000.0);
            assertEquals(120000.0, presupuesto.getSaldo(), 0.01);
            
            // Gastar
            presupuesto.gastar(30000.0);
            assertEquals(90000.0, presupuesto.getSaldo(), 0.01);
            
            // Processarjornada (victoria casa)
            presupuesto.procesarJornadaCompleta(true, false, true);
            assertEquals(97500.0, presupuesto.getSaldo(), 0.01);
            
            // Pagar salaris
            presupuesto.setGastosMantenimiento(10000.0);
            presupuesto.pagarSalarios();
            assertEquals(87500.0, presupuesto.getSaldo(), 0.01);
            
        } catch (SaldoInsuficienteException e) {
            fail("No hauria de llançar excepció: " + e.getMessage());
        }
    }
}
