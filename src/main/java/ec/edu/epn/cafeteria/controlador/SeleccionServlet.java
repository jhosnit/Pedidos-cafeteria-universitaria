package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.modelo.Seleccion;
import ec.edu.epn.cafeteria.servicio.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Seleccion temporal del cliente (CU-06): agregar, cambiar cantidad y quitar.
 *
 * @since 1.0
 */
@WebServlet("/cliente/seleccion")
public class SeleccionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient PedidoService pedidoService = new PedidoService();

    /**
     * Devuelve la seleccion guardada en sesion, creandola si todavia no existe.
     *
     * @param req peticion actual
     * @return seleccion del cliente
     */
    static Seleccion seleccion(HttpServletRequest req) {
        HttpSession sesion = req.getSession();
        Seleccion s = (Seleccion) sesion.getAttribute("seleccion");
        if (s == null) {
            s = new Seleccion();
            sesion.setAttribute("seleccion", s);
        }
        return s;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("sel", seleccion(req));
        Web.mostrar(req, resp, "cliente/seleccion");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Seleccion sel = seleccion(req);
        String accion = req.getParameter("accion");
        int productoId = Web.entero(req.getParameter("productoId"));
        String volver = "/cliente/seleccion";
        try {
            switch (accion == null ? "" : accion) {
                case "agregar" -> {
                    pedidoService.agregar(sel, productoId, req.getParameter("cantidad"));
                    Web.flash(req, true, "Producto agregado a tu selección.");
                    volver = "/cliente/menu";
                }
                case "cambiar" -> pedidoService.cambiarCantidad(sel, productoId, req.getParameter("cantidad"));
                case "quitar" -> sel.quitar(productoId);
                default -> {
                }
            }
        } catch (ReglaNegocioException e) {
            Web.flash(req, false, e.getMessage());
            if ("agregar".equals(accion)) {
                volver = "/cliente/menu";
            }
        }
        Web.redirigir(req, resp, volver);
    }
}
