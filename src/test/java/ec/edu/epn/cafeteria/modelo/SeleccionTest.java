package ec.edu.epn.cafeteria.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la seleccion temporal (HU-05 y HU-06).
 */
class SeleccionTest {

    private Seleccion seleccion;
    private Producto cafe;
    private Producto sanduche;

    @BeforeEach
    void preparar() {
        seleccion = new Seleccion();
        cafe = new Producto(1, "Café", new BigDecimal("1.50"), true);
        sanduche = new Producto(2, "Sánduche", new BigDecimal("2.25"), true);
    }

    @Test
    void totalEsLaSumaDePrecioPorCantidad() {
        // Ejemplo del criterio CA-06.1: 2 x 1.50 + 1 x 2.25 = 5.25
        seleccion.agregar(cafe, 2);
        seleccion.agregar(sanduche, 1);

        assertEquals(new BigDecimal("5.25"), seleccion.getTotal());
        assertEquals(2, seleccion.getItems().size());
    }

    @Test
    void seleccionVaciaTieneTotalCeroConDosDecimales() {
        assertTrue(seleccion.isVacia());
        assertEquals(new BigDecimal("0.00"), seleccion.getTotal());
    }

    @Test
    void agregarElMismoProductoSumaLaCantidad() {
        seleccion.agregar(cafe, 3);
        seleccion.agregar(cafe, 4);

        assertEquals(7, seleccion.getItems().get(0).getCantidad());
    }

    @Test
    void laSumaNoPuedePasarDeDiez() {
        seleccion.agregar(cafe, 8);

        assertThrows(ReglaNegocioException.class, () -> seleccion.agregar(cafe, 3));
        assertEquals(8, seleccion.getItems().get(0).getCantidad());
    }

    @Test
    void cantidadFueraDeRangoSeRechaza() {
        assertThrows(ReglaNegocioException.class, () -> seleccion.agregar(cafe, 0));
        assertThrows(ReglaNegocioException.class, () -> seleccion.agregar(cafe, 11));
        assertThrows(ReglaNegocioException.class, () -> seleccion.agregar(cafe, -1));
        assertTrue(seleccion.isVacia());
    }

    @Test
    void productoNoDisponibleNoSePuedeAgregar() {
        cafe.setDisponible(false);

        assertThrows(ReglaNegocioException.class, () -> seleccion.agregar(cafe, 1));
        assertTrue(seleccion.isVacia());
    }

    @Test
    void cambiarCantidadYQuitarRecalculanElTotal() {
        seleccion.agregar(cafe, 2);
        seleccion.agregar(sanduche, 1);

        seleccion.cambiarCantidad(1, 4);
        assertEquals(new BigDecimal("8.25"), seleccion.getTotal());

        seleccion.quitar(2);
        assertEquals(new BigDecimal("6.00"), seleccion.getTotal());
    }
}
