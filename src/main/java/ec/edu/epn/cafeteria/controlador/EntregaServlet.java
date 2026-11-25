package ec.edu.epn.cafeteria.controlador;

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
 * Entrega con codigo de retiro (CU-15). Primero se busca el pedido y se muestra
 * el resumen; la entrega solo se registra cuando el personal confirma.
 *
 * @since 1.0
 */
@WebServlet("/personal/entrega")
public class EntregaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Web.mostrar(req, resp, "personal/entrega");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("confirmar".equals(req.getParameter("accion"))) {
            try {
                pedidoService.entregar(Web.entero(req.getParameter("id")));
                Web.flash(req, true, "Pedido " + req.getParameter("codigo") + " entregado.");
            } catch (ReglaNegocioException e) {
                Web.flash(req, false, e.getMessage());
            }
            Web.redirigir(req, resp, "/personal/pedidos?estado=LISTO_PARA_RETIRAR");
            return;
        }

        String codigo = req.getParameter("codigo");
        req.setAttribute("codigo", codigo);
        try {
            Pedido pedido = pedidoService.buscarParaEntrega(codigo);
            req.setAttribute("pedido", pedido);
            Web.mostrar(req, resp, "personal/entrega-resumen");
        } catch (ReglaNegocioException e) {
            req.setAttribute("error", e.getMessage());
            Web.mostrar(req, resp, "personal/entrega");
        }
    }
}
