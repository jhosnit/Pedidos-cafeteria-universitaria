package ec.edu.epn.cafeteria.dao;

/**
 * Error inesperado al hablar con la base de datos. Envuelve la SQLException
 * para no obligar a los servlets a declararla.
 *
 * @since 1.0
 */
public class DatosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Codigo SQLState de PostgreSQL para violacion de restriccion UNIQUE. */
    public static final String VIOLACION_UNICA = "23505";

    /**
     * @param mensaje descripcion de la operacion que fallo
     * @param causa   error original de JDBC
     */
    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
