package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.servicio.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Menu que ve el cliente (CU-05). Se consulta en cada carga, asi que siempre
 * muestra precios y disponibilidad actuales (CA-04.4).
 *
 * @since 1.0
 */
@WebServlet("/cliente/menu")
public class MenuServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient ProductoService productoService = new ProductoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("productos", productoService.listar());
        Web.mostrar(req, resp, "cliente/menu");
    }
}
