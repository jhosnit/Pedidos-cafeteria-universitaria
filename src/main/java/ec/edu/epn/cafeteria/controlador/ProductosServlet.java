package ec.edu.epn.cafeteria.controlador;

import ec.edu.epn.cafeteria.modelo.Producto;
import ec.edu.epn.cafeteria.servicio.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Gestion de productos para el personal (CU-09, CU-10, CU-11).
 * <ul>
 *   <li>GET /personal/productos: lista</li>
 *   <li>POST /personal/productos: cambia la disponibilidad desde la lista</li>
 *   <li>GET/POST /personal/productos/form: crear o editar (con ?id)</li>
 * </ul>
 *
 * @since 1.0
 */
@WebServlet({"/personal/productos", "/personal/productos/form"})
public class ProductosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient ProductoService productoService = new ProductoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!esFormulario(req)) {
            req.setAttribute("productos", productoService.listar());
            Web.mostrar(req, resp, "personal/productos");
            return;
        }
        int id = Web.entero(req.getParameter("id"));
        Producto producto = id > 0 ? productoService.buscar(id) : new Producto();
        if (producto == null) {
            Web.flash(req, false, "El producto no existe.");
            Web.redirigir(req, resp, "/personal/productos");
            return;
        }
        req.setAttribute("producto", producto);
        req.setAttribute("precioTexto", producto.getPrecio() == null ? "" : producto.getPrecio().toPlainString());
        Web.mostrar(req, resp, "personal/producto-form");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!esFormulario(req)) {
            Producto p = productoService.alternarDisponibilidad(Web.entero(req.getParameter("id")));
            if (p != null) {
                Web.flash(req, true, p.getNombre() + (p.isDisponible() ? " ahora está disponible." : " quedó como no disponible."));
            }
            Web.redirigir(req, resp, "/personal/productos");
            return;
        }

        int id = Web.entero(req.getParameter("id"));
        String nombre = req.getParameter("nombre");
        String precio = req.getParameter("precio");
        List<String> errores = id > 0
                ? productoService.actualizar(id, nombre, precio)
                : productoService.registrar(nombre, precio, req.getParameter("noDisponible") == null);
        if (!errores.isEmpty()) {
            Producto p = new Producto();
            p.setId(Math.max(id, 0));
            p.setNombre(nombre);
            p.setDisponible(req.getParameter("noDisponible") == null);
            req.setAttribute("producto", p);
            req.setAttribute("precioTexto", precio);
            req.setAttribute("errores", errores);
            Web.mostrar(req, resp, "personal/producto-form");
            return;
        }
        Web.flash(req, true, id > 0 ? "Producto actualizado." : "Producto registrado.");
        Web.redirigir(req, resp, "/personal/productos");
    }

    private boolean esFormulario(HttpServletRequest req) {
        return req.getServletPath().endsWith("/form");
    }
}
