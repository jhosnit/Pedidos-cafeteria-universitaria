package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Cliente;
import ec.edu.epn.cafeteria.servicio.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Registro de cuenta de cliente (CU-01).
 *
 * @since 1.0
 */
@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("cliente", new Cliente());
        Web.mostrar(req, resp, "registro");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cliente c = new Cliente();
        c.setNombre(limpiar(req.getParameter("nombre")));
        c.setApellido(limpiar(req.getParameter("apellido")));
        c.setCedulaRuc(limpiar(req.getParameter("cedulaRuc")));
        c.setCelular(limpiar(req.getParameter("celular")));
        c.setCorreo(limpiar(req.getParameter("correo")));

        List<String> errores = authService.registrarCliente(c,
                req.getParameter("contrasena"), req.getParameter("confirmacion"));
        if (!errores.isEmpty()) {
            req.setAttribute("errores", errores);
            req.setAttribute("cliente", c);
            Web.mostrar(req, resp, "registro");
            return;
        }
        Web.flash(req, true, "Tu cuenta fue creada con el código " + c.getCodigoCliente() + ". Ya puedes iniciar sesión.");
        Web.redirigir(req, resp, "/login");
    }

    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
