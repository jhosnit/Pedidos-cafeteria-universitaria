package ec.edu.epn.cafeteria.servicio;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Formato del codigo de retiro (CA-07.2) y lectura tolerante (CA-15.4).
 */
class CodigoRetiroTest {

    @RepeatedTest(200)
    void elCodigoGeneradoTieneFormatoLetraLetraGuionDigitoDigito() {
        assertTrue(CodigoRetiro.generar().matches("[A-Z]{2}-\\d{2}"));
    }

    @Test
    void normalizarAceptaMinusculasYSinGuion() {
        assertEquals("AB-01", CodigoRetiro.normalizar("ab01"));
        assertEquals("AB-01", CodigoRetiro.normalizar("Ab-01"));
        assertEquals("AB-01", CodigoRetiro.normalizar("  ab-01 "));
    }

    @Test
    void normalizarRechazaTextoSinFormatoDeCodigo() {
        assertNull(CodigoRetiro.normalizar(null));
        assertNull(CodigoRetiro.normalizar(""));
        assertNull(CodigoRetiro.normalizar("A1-01"));
        assertNull(CodigoRetiro.normalizar("ABC-01"));
    }
}
