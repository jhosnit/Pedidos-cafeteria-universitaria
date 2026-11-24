package ec.edu.epn.cafeteria.servicio;

import java.math.BigDecimal;

/**
 * Validaciones de formato que se hacen en el servidor aunque el formulario
 * tambien las tenga (RNF-04).
 *
 * @since 1.0
 */
public final class Validaciones {

    /** Dominio de correo aceptado (SUP-05). */
    public static final String DOMINIO = "@epn.edu.ec";

    private Validaciones() {
    }

    /**
     * @param texto valor recibido del formulario
     * @return true si es null o solo tiene espacios
     */
    public static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    /**
     * @param correo correo a revisar
     * @return true si tiene formato de correo y termina en @epn.edu.ec
     */
    public static boolean correoInstitucional(String correo) {
        return correo != null
                && correo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                && correo.toLowerCase().endsWith(DOMINIO);
    }

    /**
     * @param celular numero a revisar
     * @return true si son exactamente 10 digitos (SUP-07)
     */
    public static boolean celular(String celular) {
        return celular != null && celular.matches("\\d{10}");
    }

    /**
     * @param contrasena contrasena a revisar
     * @return true si tiene al menos 8 caracteres (SUP-08)
     */
    public static boolean contrasena(String contrasena) {
        return contrasena != null && contrasena.length() >= 8;
    }

    /**
     * Convierte el precio del formulario. Debe ser mayor que 0 y tener como maximo
     * dos decimales (CA-09.3). Acepta coma o punto como separador.
     *
     * @param texto valor del campo precio
     * @return el precio con escala 2, o {@code null} si no es valido
     */
    public static BigDecimal precio(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim().replace(',', '.');
        if (!limpio.matches("\\d{1,6}(\\.\\d{1,2})?")) {
            return null;
        }
        BigDecimal valor = new BigDecimal(limpio);
        return valor.signum() > 0 ? valor.setScale(2) : null;
    }
}
