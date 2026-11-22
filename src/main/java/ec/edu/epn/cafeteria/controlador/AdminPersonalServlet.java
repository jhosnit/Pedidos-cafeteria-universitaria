package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Usuario;
import ec.edu.epn.cafeteria.servicio.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Gestion de cuentas de personal (CU-04). La lista esta en /admin/personal y el
 * formulario de registro en /admin/personal/nuevo.
 *
 * @since 1.0
 */
@WebServlet({"/admin/personal", "/admin/personal/nuevo"})
public class AdminPersonalServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().endsWith("/nuevo")) {
            req.setAttribute("personal", new Usuario());
            Web.mostrar(req, resp, "admin/personal-form");
            return;
        }
        req.setAttribute("lista", authService.listarPersonal());
        Web.mostrar(req, resp, "admin/personal");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario p = new Usuario();
        p.setNombre(trim(req.getParameter("nombre")));
        p.setApellido(trim(req.getParameter("apellido")));
        p.setCorreo(trim(req.getParameter("correo")));

        List<String> errores = authService.registrarPersonal(p,
                req.getParameter("contrasena"), req.getParameter("confirmacion"));
        if (!errores.isEmpty()) {
            req.setAttribute("errores", errores);
            req.setAttribute("personal", p);
            Web.mostrar(req, resp, "admin/personal-form");
            return;
        }
        Web.flash(req, true, "Se registró la cuenta de " + p.getNombreCompleto() + ".");
        Web.redirigir(req, resp, "/admin/personal");
    }

    private String trim(String valor) {
        return valor == null ? null : valor.trim();
    }
}
