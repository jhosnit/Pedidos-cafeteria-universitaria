package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Rol;
import ec.edu.epn.cafeteria.modelo.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Control de acceso en el servidor (RNF-03). Toda peticion pasa por aqui, asi que
 * escribir la URL a mano no sirve para saltarse el rol.
 *
 * <ul>
 *   <li>/cliente/* solo para clientes</li>
 *   <li>/personal/* solo para el personal de cafeteria</li>
 *   <li>/admin/* solo para el administrador</li>
 * </ul>
 *
 * @since 1.0
 */
@WebFilter("/*")
public class AuthFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        String ruta = req.getRequestURI().substring(req.getContextPath().length());
        Usuario usuario = Web.usuario(req);

        if (ruta.startsWith("/assets/") || ruta.equals("/favicon.ico")) {
            chain.doFilter(req, resp);
            return;
        }
        if (ruta.isEmpty() || ruta.equals("/") || ruta.equals("/login") || ruta.equals("/registro")) {
            // Con sesion abierta no tiene sentido volver al login: se va a su pantalla inicial
            if (usuario != null) {
                Web.redirigir(req, resp, usuario.getRol().getInicio());
            } else if (ruta.isEmpty() || ruta.equals("/")) {
                Web.redirigir(req, resp, "/login");
            } else {
                chain.doFilter(req, resp);
            }
            return;
        }
        if (ruta.equals("/logout")) {
            chain.doFilter(req, resp);
            return;
        }
        if (usuario == null) {
            Web.redirigir(req, resp, "/login");
            return;
        }
        Rol requerido = rolRequerido(ruta);
        if (requerido != null && requerido != usuario.getRol()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Web.mostrar(req, resp, "acceso-denegado");
            return;
        }
        // Evita que con el boton atras se vea una pagina protegida despues de cerrar sesion
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(req, resp);
    }

    private Rol rolRequerido(String ruta) {
        if (ruta.startsWith("/cliente/")) return Rol.CLIENTE;
        if (ruta.startsWith("/personal/")) return Rol.PERSONAL;
        if (ruta.startsWith("/admin/")) return Rol.ADMINISTRADOR;
        return null;
    }
}
