package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.EstadoPedido;
import ec.edu.epn.cafeteria.modelo.Pedido;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.servicio.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Panel de pedidos del personal (CU-12, CU-13, CU-14).
 * <ul>
 *   <li>GET ?estado=PENDIENTE|...|TODOS filtra la lista; por defecto Pendiente</li>
 *   <li>GET ?id=N muestra el detalle</li>
 *   <li>POST id + estado avanza el pedido al siguiente estado</li>
 * </ul>
 *
 * @since 1.0
 */
@WebServlet("/personal/pedidos")
public class PanelPedidosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Web.entero(req.getParameter("id"));
        if (id > 0) {
            Pedido pedido = pedidoService.detalle(id);
            if (pedido == null) {
                Web.flash(req, false, "El pedido no existe.");
                Web.redirigir(req, resp, "/personal/pedidos");
                return;
            }
            req.setAttribute("pedido", pedido);
            Web.mostrar(req, resp, "personal/pedido-detalle");
            return;
        }
        String filtro = req.getParameter("estado");
        EstadoPedido estado = leerEstado(filtro == null ? "PENDIENTE" : filtro);
        req.setAttribute("filtro", estado == null ? "TODOS" : estado.name());
        req.setAttribute("estados", EstadoPedido.values());
        req.setAttribute("pedidos", pedidoService.listarPorEstado(estado));
        Web.mostrar(req, resp, "personal/pedidos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Web.entero(req.getParameter("id"));
        EstadoPedido actual = leerEstado(req.getParameter("estado"));
        if (actual == null) {
            Web.redirigir(req, resp, "/personal/pedidos");
            return;
        }
        try {
            EstadoPedido nuevo = pedidoService.avanzar(id, actual);
            Web.flash(req, true, "El pedido pasó a " + nuevo.getEtiqueta() + ".");
            Web.redirigir(req, resp, "/personal/pedidos?estado=" + actual.name());
        } catch (ReglaNegocioException e) {
            Web.flash(req, false, e.getMessage());
            Web.redirigir(req, resp, "/personal/pedidos?estado=TODOS");
        }
    }

    private EstadoPedido leerEstado(String texto) {
        try {
            return EstadoPedido.valueOf(texto);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
