package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Pedido;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.modelo.Seleccion;
import ec.edu.epn.cafeteria.servicio.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Revision final y confirmacion del pedido (CU-07).
 *
 * @since 1.0
 */
@WebServlet("/cliente/confirmar")
public class ConfirmarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Seleccion sel = SeleccionServlet.seleccion(req);
        if (sel.isVacia()) {
            Web.flash(req, false, "Tu selección está vacía. Agrega al menos un producto.");
            Web.redirigir(req, resp, "/cliente/menu");
            return;
        }
        req.setAttribute("sel", sel);
        Web.mostrar(req, resp, "cliente/revision");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Seleccion sel = SeleccionServlet.seleccion(req);
        try {
            Pedido pedido = pedidoService.confirmar(sel, Web.usuario(req).getId());
            req.setAttribute("pedido", pedido);
            Web.mostrar(req, resp, "cliente/confirmado");
        } catch (ReglaNegocioException e) {
            // La seleccion se conserva; si fue por precio, ya tiene los precios nuevos
            Web.flash(req, false, e.getMessage());
            Web.redirigir(req, resp, sel.isVacia() ? "/cliente/menu" : "/cliente/confirmar");
        }
    }
}
