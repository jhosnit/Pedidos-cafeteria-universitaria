package ec.edu.epn.cafeteria.servicio;

import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Formato del codigo de retiro (CA-07.2).
 */
class CodigoRetiroTest {

    @RepeatedTest(200)
    void elCodigoGeneradoTieneFormatoLetraLetraGuionDigitoDigito() {
        assertTrue(CodigoRetiro.generar().matches("[A-Z]{2}-\\d{2}"));
    }
}
