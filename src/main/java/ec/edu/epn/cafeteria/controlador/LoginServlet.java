package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Usuario;
import ec.edu.epn.cafeteria.servicio.AuthService;
import ec.edu.epn.cafeteria.servicio.Validaciones;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Inicio de sesion (CU-02).
 *
 * @since 1.0
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Web.mostrar(req, resp, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String correo = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        req.setAttribute("correo", correo);

        if (Validaciones.vacio(correo) || Validaciones.vacio(contrasena)) {
            req.setAttribute("error", "Completa el correo y la contraseña.");
            Web.mostrar(req, resp, "login");
            return;
        }
        Usuario usuario = authService.autenticar(correo, contrasena);
        if (usuario == null) {
            // Mensaje generico a proposito: no se dice si fallo el correo o la contrasena
            req.setAttribute("error", "Correo o contraseña incorrectos.");
            Web.mostrar(req, resp, "login");
            return;
        }
        // Sesion nueva al autenticar para evitar fijacion de sesion
        HttpSession anterior = req.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        req.getSession(true).setAttribute(Web.USUARIO, usuario);
        Web.redirigir(req, resp, usuario.getRol().getInicio());
    }
}
