package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Utilidades que comparten todos los servlets: mostrar vistas JSP, mensajes
 * de una sola vez (flash) y lectura de parametros.
 *
 * @since 1.0
 */
public final class Web {

    /** Atributo de sesion donde vive el usuario autenticado. */
    public static final String USUARIO = "usuario";

    private Web() {
    }

    /**
     * Muestra una vista ubicada en /WEB-INF/vistas. Las JSP quedan dentro de
     * WEB-INF para que no se puedan abrir directamente desde el navegador.
     *
     * @param req   peticion actual
     * @param resp  respuesta actual
     * @param vista ruta sin extension, por ejemplo "cliente/menu"
     * @throws ServletException si la JSP falla
     * @throws IOException      si no se puede escribir la respuesta
     */
    public static void mostrar(HttpServletRequest req, HttpServletResponse resp, String vista)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/vistas/" + vista + ".jsp").forward(req, resp);
    }

    /**
     * Redirige a una ruta de la aplicacion (patron POST-redirect-GET).
     *
     * @param req  peticion actual
     * @param resp respuesta actual
     * @param ruta ruta que empieza con "/"
     * @throws IOException si no se puede enviar la redireccion
     */
    public static void redirigir(HttpServletRequest req, HttpServletResponse resp, String ruta) throws IOException {
        resp.sendRedirect(req.getContextPath() + ruta);
    }

    /**
     * Guarda un mensaje que se muestra una sola vez en la siguiente pantalla.
     *
     * @param req     peticion actual
     * @param exito   true para mensaje verde, false para mensaje de error
     * @param mensaje texto a mostrar
     */
    public static void flash(HttpServletRequest req, boolean exito, String mensaje) {
        req.getSession().setAttribute(exito ? "flashOk" : "flashError", mensaje);
    }

    /**
     * @param req peticion actual
     * @return el usuario en sesion o {@code null}
     */
    public static Usuario usuario(HttpServletRequest req) {
        var sesion = req.getSession(false);
        return sesion == null ? null : (Usuario) sesion.getAttribute(USUARIO);
    }

    /**
     * @param texto valor de un parametro
     * @return el numero, o -1 si no es un entero
     */
    public static int entero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }
}
