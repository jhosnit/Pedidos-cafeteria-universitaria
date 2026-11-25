package ec.edu.epn.cafeteria.servicio;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Genera codigos de retiro con formato LL-DD, por ejemplo AB-01 (SUP-09).
 * La unicidad la garantiza la base de datos; si sale repetido se genera otro
 * (SUP-19).
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
}
