package ec.edu.epn.cafeteria.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Orden de los estados del pedido (RN-NEG-06).
 */
class EstadoPedidoTest {

    @Test
    void losEstadosAvanzanEnOrdenSinSaltos() {
        assertEquals(EstadoPedido.EN_PREPARACION, EstadoPedido.PENDIENTE.siguiente());
        assertEquals(EstadoPedido.LISTO_PARA_RETIRAR, EstadoPedido.EN_PREPARACION.siguiente());
        assertEquals(EstadoPedido.ENTREGADO, EstadoPedido.LISTO_PARA_RETIRAR.siguiente());
    }

    @Test
    void entregadoEsElEstadoFinal() {
        assertNull(EstadoPedido.ENTREGADO.siguiente());
    }
}
