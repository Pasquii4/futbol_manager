package test.model;

import model.Presupuesto;
import model.SaldoInsuficienteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaris per a la classe Presupuesto
 */
public class PresupuestoTest {
    
    private Presupuesto presupuesto;
    
    @BeforeEach
    public void setUp() {
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
    public void testIngresar() {
        presupuesto.ingresar(25000.0);
        assertEquals(125000.0, presupuesto.getSaldo(), 0.01, 
                    "El saldo hauria de ser 125000€ després d'ingressar 25000€");
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
    
    }
}
