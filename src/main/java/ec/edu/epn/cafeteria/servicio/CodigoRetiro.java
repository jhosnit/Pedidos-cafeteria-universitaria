package ec.edu.epn.cafeteria.servicio;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Genera y normaliza codigos de retiro con formato LL-DD, por ejemplo AB-01 (SUP-09).
 * La unicidad la garantiza la base de datos; si sale repetido se genera otro (SUP-19).
 *
 * @since 1.0
 */
public final class CodigoRetiro {

    private CodigoRetiro() {
    }

    /**
     * @return codigo aleatorio con dos letras A-Z, guion y dos digitos
     */
    public static String generar() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        char l1 = (char) ('A' + r.nextInt(26));
        char l2 = (char) ('A' + r.nextInt(26));
        return String.format("%c%c-%02d", l1, l2, r.nextInt(100));
    }

    /**
     * Acepta el codigo como lo escriba el personal: en minusculas, con o sin guion
     * y con espacios alrededor (CA-15.4). Por ejemplo "ab01" se convierte en "AB-01".
     *
     * @param texto codigo ingresado
     * @return codigo con formato LL-DD, o {@code null} si no tiene forma de codigo
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.replaceAll("[\\s-]", "").toUpperCase();
        if (!limpio.matches("[A-Z]{2}\\d{2}")) {
            return null;
        }
        return limpio.substring(0, 2) + "-" + limpio.substring(2);
    }
}
