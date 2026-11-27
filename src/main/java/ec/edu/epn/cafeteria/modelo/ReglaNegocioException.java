package ec.edu.epn.cafeteria.modelo;

/**
 * Se lanza cuando una operacion incumple una regla de negocio. El mensaje
 * esta pensado para mostrarse tal cual al usuario.
 *
 * @since 1.0
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicacion para el usuario
     */
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
