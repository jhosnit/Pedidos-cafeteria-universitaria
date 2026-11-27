package ec.edu.epn.cafeteria.servicio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Casos de la validacion de cedula y RUC (CA-01.4).
 */
class ValidadorIdentificacionTest {

    @Test
    void cedulaValidaPasa() {
        assertTrue(ValidadorIdentificacion.esValida("1710034065"));
        assertTrue(ValidadorIdentificacion.esValida("0102030400"));
    }

    @Test
    void cedulaConDigitoVerificadorIncorrectoNoPasa() {
        assertFalse(ValidadorIdentificacion.esValida("1710034066"));
    }

    @Test
    void cedulaConProvinciaInexistenteNoPasa() {
        assertFalse(ValidadorIdentificacion.esValida("2510034065"));
    }

    @Test
    void textoQueNoSonDiezOTreceDigitosNoPasa() {
        assertFalse(ValidadorIdentificacion.esValida(null));
        assertFalse(ValidadorIdentificacion.esValida(""));
        assertFalse(ValidadorIdentificacion.esValida("17100340"));
        assertFalse(ValidadorIdentificacion.esValida("17100340AB"));
    }

    @Test
    void rucDePersonaNaturalPasa() {
        assertTrue(ValidadorIdentificacion.esValida("1710034065001"));
    }

    @Test
    void rucDeSociedadPrivadaPasa() {
        assertTrue(ValidadorIdentificacion.esValida("1790012344001"));
    }

    @Test
    void rucDeEntidadPublicaPasa() {
        assertTrue(ValidadorIdentificacion.esValida("1760001040001"));
    }

    @Test
    void rucConEstablecimientoCeroNoPasa() {
        assertFalse(ValidadorIdentificacion.esValida("1710034065000"));
    }
}
