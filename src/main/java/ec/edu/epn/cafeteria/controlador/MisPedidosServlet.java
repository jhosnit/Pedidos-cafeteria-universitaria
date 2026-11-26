package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Pedido;
import ec.edu.epn.cafeteria.servicio.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Pedidos del cliente y su detalle (CU-08). Con ?id muestra un pedido;
 * si el pedido es de otra persona se responde como si no existiera.
 *
 * @since 1.0
 */
@WebServlet("/cliente/pedidos")
public class MisPedidosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int clienteId = Web.usuarioEnSesion(req).getId();
        int id = Web.entero(req.getParameter("id"));
        if (id > 0) {
            Pedido pedido = pedidoService.detalleDeCliente(id, clienteId);
            if (pedido == null) {
                Web.flash(req, false, "No encontramos ese pedido.");
                Web.redirigir(req, resp, "/cliente/pedidos");
                return;
            }
            req.setAttribute("pedido", pedido);
            Web.mostrar(req, resp, "cliente/pedido-detalle");
            return;
        }
        req.setAttribute("pedidos", pedidoService.misPedidos(clienteId));
        Web.mostrar(req, resp, "cliente/pedidos");
    }
}
