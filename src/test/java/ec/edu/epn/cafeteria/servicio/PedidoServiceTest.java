package ec.edu.epn.cafeteria.servicio;

import ec.edu.epn.cafeteria.dao.PedidoDao;
import ec.edu.epn.cafeteria.dao.ProductoDao;
import ec.edu.epn.cafeteria.modelo.EstadoPedido;
import ec.edu.epn.cafeteria.modelo.Pedido;
import ec.edu.epn.cafeteria.modelo.Producto;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.modelo.Seleccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de seleccion y confirmacion del pedido (HU-05 a HU-08).
 */
class PedidoServiceTest {

    static class ProductosFalsos extends ProductoDao {
        final Map<Integer, Producto> productos = new HashMap<>();

        @Override
        public Producto buscarPorId(int id) {
            Producto p = productos.get(id);
            // copia, para simular que cada consulta trae datos frescos de la base
            return p == null ? null : new Producto(p.getId(), p.getNombre(), p.getPrecio(), p.isDisponible());
        }
    }

    static class PedidosFalsos extends PedidoDao {
        final List<Pedido> guardados = new ArrayList<>();
        int codigosOcupados;

        @Override
        public boolean insertar(Pedido pedido) {
            if (codigosOcupados > 0) {
                codigosOcupados--;
                return false;
            }
            pedido.setId(guardados.size() + 1);
            guardados.add(pedido);
            return true;
        }

        @Override
        public List<Pedido> listarPorCliente(int clienteId) {
            return guardados.stream().filter(p -> p.getClienteId() == clienteId).toList();
        }

        @Override
        public Pedido buscarPorId(int id) {
            return guardados.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        }
    }

    private ProductosFalsos productos;
    private PedidosFalsos pedidos;
    private PedidoService servicio;
    private Seleccion seleccion;

    @BeforeEach
    void preparar() {
        productos = new ProductosFalsos();
        productos.productos.put(1, new Producto(1, "Café", new BigDecimal("1.50"), true));
        productos.productos.put(2, new Producto(2, "Sánduche", new BigDecimal("2.25"), true));
        productos.productos.put(3, new Producto(3, "Jugo", new BigDecimal("1.00"), false));
        pedidos = new PedidosFalsos();
        servicio = new PedidoService(pedidos, productos);
        seleccion = new Seleccion();
    }

    @Test
    void cantidadNoNumericaODecimalSeRechaza() {
        for (String cantidad : new String[] { "", "abc", "1.5", "0", "-2", "11", null }) {
            assertThrows(ReglaNegocioException.class, () -> servicio.agregar(seleccion, 1, cantidad),
                    "Deberia rechazar " + cantidad);
        }
        assertTrue(seleccion.isVacia());
    }

    @Test
    void productoNoDisponibleNoSeAgrega() {
        assertThrows(ReglaNegocioException.class, () -> servicio.agregar(seleccion, 3, "1"));
    }

    @Test
    void confirmarCreaPedidoPendienteConCodigoYVaciaLaSeleccion() {
        servicio.agregar(seleccion, 1, "2");
        servicio.agregar(seleccion, 2, "1");

        Pedido pedido = servicio.confirmar(seleccion, 7);

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(new BigDecimal("5.25"), pedido.getImporteTotal());
        assertTrue(pedido.getCodigoRetiro().matches("[A-Z]{2}-\\d{2}"));
        assertEquals(2, pedido.getDetalles().size());
        assertEquals(7, pedido.getClienteId());
        assertTrue(seleccion.isVacia());
    }

    @Test
    void seleccionVaciaNoSePuedeConfirmar() {
        assertThrows(ReglaNegocioException.class, () -> servicio.confirmar(seleccion, 7));
        assertTrue(pedidos.guardados.isEmpty());
    }

    @Test
    void productoQueDejoDeEstarDisponibleImpideConfirmar() {
        servicio.agregar(seleccion, 1, "1");
        productos.productos.get(1).setDisponible(false);

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.confirmar(seleccion, 7));

        assertTrue(e.getMessage().contains("Café"));
        assertTrue(pedidos.guardados.isEmpty());
        assertFalse(seleccion.isVacia());
    }

    @Test
    void precioModificadoPideRevisarYLuegoSePuedeConfirmar() {
        servicio.agregar(seleccion, 1, "2");
        productos.productos.get(1).setPrecio(new BigDecimal("1.75"));

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.confirmar(seleccion, 7));
        assertTrue(e.getMessage().contains("3.50"));
        assertTrue(pedidos.guardados.isEmpty());

        // segunda confirmacion, ya con el precio nuevo
        Pedido pedido = servicio.confirmar(seleccion, 7);
        assertEquals(new BigDecimal("3.50"), pedido.getImporteTotal());
    }

    @Test
    void elMismoPrecioConOtraEscalaNoCuentaComoCambio() {
        servicio.agregar(seleccion, 1, "1");
        productos.productos.get(1).setPrecio(new BigDecimal("1.5"));

        Pedido pedido = servicio.confirmar(seleccion, 7);

        assertEquals(1, pedido.getId());
    }

    @Test
    void elPrecioDelPedidoNoCambiaSiDespuesCambiaElMenu() {
        servicio.agregar(seleccion, 2, "1");
        Pedido pedido = servicio.confirmar(seleccion, 7);

        productos.productos.get(2).setPrecio(new BigDecimal("9.99"));

        assertEquals(new BigDecimal("2.25"), pedido.getDetalles().get(0).getPrecioRegistrado());
        assertEquals(new BigDecimal("2.25"), pedido.getImporteTotal());
    }

    @Test
    void siElCodigoYaExisteSeGeneraOtro() {
        pedidos.codigosOcupados = 3;
        servicio.agregar(seleccion, 1, "1");

        Pedido pedido = servicio.confirmar(seleccion, 7);

        assertEquals(1, pedidos.guardados.size());
        assertTrue(pedido.getCodigoRetiro().matches("[A-Z]{2}-\\d{2}"));
    }

    @Test
    void elClienteSoloVeSusPropiosPedidos() {
        servicio.agregar(seleccion, 1, "1");
        Pedido mio = servicio.confirmar(seleccion, 7);
        servicio.agregar(seleccion, 2, "1");
        Pedido ajeno = servicio.confirmar(seleccion, 8);

        assertEquals(1, servicio.misPedidos(7).size());
        assertEquals(mio.getId(), servicio.detalleDeCliente(mio.getId(), 7).getId());
        assertNull(servicio.detalleDeCliente(ajeno.getId(), 7));
    }
}
