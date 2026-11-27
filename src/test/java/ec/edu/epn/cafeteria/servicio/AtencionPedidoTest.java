package ec.edu.epn.cafeteria.servicio;

import ec.edu.epn.cafeteria.dao.PedidoDao;
import ec.edu.epn.cafeteria.dao.ProductoDao;
import ec.edu.epn.cafeteria.modelo.EstadoPedido;
import ec.edu.epn.cafeteria.modelo.Pedido;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la atencion de pedidos por el personal (HU-12 a HU-15).
 */
class AtencionPedidoTest {

    /** Simula la tabla pedido, incluido el UPDATE condicionado por estado. */
    static class PedidosEnMemoria extends PedidoDao {
        final List<Pedido> pedidos = new ArrayList<>();

        Pedido nuevo(int id, String codigo, EstadoPedido estado) {
            Pedido p = new Pedido();
            p.setId(id);
            p.setCodigoRetiro(codigo);
            p.setEstado(estado);
            p.setImporteTotal(new BigDecimal("3.00"));
            pedidos.add(p);
            return p;
        }

        @Override
        public List<Pedido> listarPorEstado(EstadoPedido estado) {
            return pedidos.stream().filter(p -> estado == null || p.getEstado() == estado).toList();
        }

        @Override
        public Pedido buscarPorCodigo(String codigoRetiro) {
            return pedidos.stream().filter(p -> p.getCodigoRetiro().equals(codigoRetiro)).findFirst().orElse(null);
        }

        @Override
        public boolean cambiarEstado(int pedidoId, EstadoPedido desde, EstadoPedido hacia) {
            for (Pedido p : pedidos) {
                if (p.getId() == pedidoId && p.getEstado() == desde) {
                    p.setEstado(hacia);
                    return true;
                }
            }
            return false;
        }
    }

    private PedidosEnMemoria dao;
    private PedidoService servicio;

    @BeforeEach
    void preparar() {
        dao = new PedidosEnMemoria();
        servicio = new PedidoService(dao, new ProductoDao());
    }

    @Test
    void filtroPorEstadoYTodos() {
        dao.nuevo(1, "AB-01", EstadoPedido.PENDIENTE);
        dao.nuevo(2, "CD-02", EstadoPedido.LISTO_PARA_RETIRAR);

        assertEquals(1, servicio.listarPorEstado(EstadoPedido.PENDIENTE).size());
        assertEquals(2, servicio.listarPorEstado(null).size());
        assertTrue(servicio.listarPorEstado(EstadoPedido.ENTREGADO).isEmpty());
    }

    @Test
    void pendientePasaAEnPreparacionYLuegoAListo() {
        Pedido p = dao.nuevo(1, "AB-01", EstadoPedido.PENDIENTE);

        assertEquals(EstadoPedido.EN_PREPARACION, servicio.avanzar(1, EstadoPedido.PENDIENTE));
        assertEquals(EstadoPedido.LISTO_PARA_RETIRAR, servicio.avanzar(1, EstadoPedido.EN_PREPARACION));
        assertEquals(EstadoPedido.LISTO_PARA_RETIRAR, p.getEstado());
    }

    @Test
    void siOtroUsuarioYaCambioElEstadoNoSeHaceNada() {
        Pedido p = dao.nuevo(1, "AB-01", EstadoPedido.PENDIENTE);
        servicio.avanzar(1, EstadoPedido.PENDIENTE);

        // el segundo empleado todavia ve el pedido como Pendiente
        assertThrows(ReglaNegocioException.class, () -> servicio.avanzar(1, EstadoPedido.PENDIENTE));
        assertEquals(EstadoPedido.EN_PREPARACION, p.getEstado());
    }

    @Test
    void noSePuedeMarcarComoListoUnPedidoPendiente() {
        Pedido p = dao.nuevo(1, "AB-01", EstadoPedido.PENDIENTE);

        assertThrows(ReglaNegocioException.class, () -> servicio.avanzar(1, EstadoPedido.EN_PREPARACION));
        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
    }

    @Test
    void laEntregaNoSeHaceDesdeElPanel() {
        dao.nuevo(1, "AB-01", EstadoPedido.LISTO_PARA_RETIRAR);

        assertThrows(ReglaNegocioException.class, () -> servicio.avanzar(1, EstadoPedido.LISTO_PARA_RETIRAR));
    }

    @Test
    void entregaExitosaConCodigoEscritoEnMinusculasYSinGuion() {
        Pedido p = dao.nuevo(1, "AB-01", EstadoPedido.LISTO_PARA_RETIRAR);

        Pedido encontrado = servicio.buscarParaEntrega("ab01");
        servicio.entregar(encontrado.getId());

        assertEquals(EstadoPedido.ENTREGADO, p.getEstado());
    }

    @Test
    void codigoVacioOInexistente() {
        ReglaNegocioException vacio = assertThrows(ReglaNegocioException.class, () -> servicio.buscarParaEntrega("  "));
        assertTrue(vacio.getMessage().startsWith("Ingresa"));

        ReglaNegocioException noExiste = assertThrows(ReglaNegocioException.class, () -> servicio.buscarParaEntrega("ZZ-99"));
        assertTrue(noExiste.getMessage().startsWith("No se encontró"));
    }

    @Test
    void pedidoQueNoEstaListoNoSePuedeEntregar() {
        Pedido p = dao.nuevo(1, "AB-01", EstadoPedido.EN_PREPARACION);

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.buscarParaEntrega("AB-01"));

        assertTrue(e.getMessage().contains("En preparación"));
        assertEquals(EstadoPedido.EN_PREPARACION, p.getEstado());
    }

    @Test
    void pedidoYaEntregadoNoAdmiteMasCambios() {
        dao.nuevo(1, "AB-01", EstadoPedido.ENTREGADO);

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.buscarParaEntrega("AB-01"));
        assertTrue(e.getMessage().contains("ya fue entregado"));
        assertThrows(ReglaNegocioException.class, () -> servicio.entregar(1));
    }

    @Test
    void entregaConcurrenteSoloFuncionaUnaVez() {
        dao.nuevo(1, "AB-01", EstadoPedido.LISTO_PARA_RETIRAR);
        servicio.entregar(1);

        assertThrows(ReglaNegocioException.class, () -> servicio.entregar(1));
    }
}
