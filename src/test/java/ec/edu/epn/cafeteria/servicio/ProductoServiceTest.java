package ec.edu.epn.cafeteria.servicio;

import ec.edu.epn.cafeteria.dao.ProductoDao;
import ec.edu.epn.cafeteria.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de las reglas del menu (HU-09, HU-10, HU-11).
 */
class ProductoServiceTest {

    /** ProductoDao que guarda en memoria. */
    static class ProductoDaoEnMemoria extends ProductoDao {
        final List<Producto> productos = new ArrayList<>();

        @Override
        public List<Producto> listar() {
            return productos;
        }

        @Override
        public Producto buscarPorId(int id) {
            return productos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        }

        @Override
        public boolean existeNombre(String nombre, int excluirId) {
            return productos.stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre) && p.getId() != excluirId);
        }

        @Override
        public void registrar(Producto p) {
            p.setId(productos.size() + 1);
            p.setCodigo(String.format("PRD-%04d", p.getId()));
            productos.add(p);
        }

        @Override
        public void actualizar(Producto p) {
            // el objeto ya es el mismo de la lista
        }

        @Override
        public void cambiarDisponibilidad(int id, boolean disponible) {
            buscarPorId(id).setDisponible(disponible);
        }
    }

    private ProductoDaoEnMemoria dao;
    private ProductoService servicio;

    @BeforeEach
    void preparar() {
        dao = new ProductoDaoEnMemoria();
        servicio = new ProductoService(dao);
    }

    @Test
    void registroExitosoQuedaDisponiblePorDefecto() {
        List<String> errores = servicio.registrar("Café americano", "1.00", true);

        assertTrue(errores.isEmpty());
        Producto p = dao.productos.get(0);
        assertEquals("PRD-0001", p.getCodigo());
        assertTrue(p.isDisponible());
        assertEquals(new BigDecimal("1.00"), p.getPrecio());
    }

    @Test
    void sePuedeRegistrarComoNoDisponible() {
        servicio.registrar("Jugo natural", "1.50", false);

        assertFalse(dao.productos.get(0).isDisponible());
    }

    @Test
    void faltaNombreOPrecio() {
        List<String> errores = servicio.registrar("  ", "", true);

        assertEquals(List.of("El nombre es obligatorio.", "El precio es obligatorio."), errores);
        assertTrue(dao.productos.isEmpty());
    }

    @Test
    void precioInvalidoSeRechaza() {
        for (String precio : new String[]{"abc", "0", "-1", "1.505", "0.00"}) {
            assertFalse(servicio.registrar("Pan", precio, true).isEmpty(), "Deberia rechazar " + precio);
        }
        assertTrue(dao.productos.isEmpty());
    }

    @Test
    void precioConComaSeAcepta() {
        assertTrue(servicio.registrar("Pan", "0,75", true).isEmpty());
        assertEquals(new BigDecimal("0.75"), dao.productos.get(0).getPrecio());
    }

    @Test
    void nombreRepetidoSinImportarMayusculasSeRechaza() {
        servicio.registrar("Café americano", "1.00", true);

        List<String> errores = servicio.registrar("CAFÉ AMERICANO", "1.20", true);

        assertEquals(List.of("Ya existe un producto con ese nombre."), errores);
    }

    @Test
    void nombreDeMasDeSetentaCaracteresSeRechaza() {
        String largo = "x".repeat(71);

        assertEquals(List.of("El nombre puede tener máximo 70 caracteres."), servicio.registrar(largo, "1", true));
        assertTrue(servicio.registrar("x".repeat(70), "1", true).isEmpty());
    }

    @Test
    void actualizarCambiaNombreYPrecio() {
        servicio.registrar("Café", "1.00", true);

        assertTrue(servicio.actualizar(1, "Café pasado", "1.25").isEmpty());

        assertEquals("Café pasado", dao.productos.get(0).getNombre());
        assertEquals(new BigDecimal("1.25"), dao.productos.get(0).getPrecio());
    }

    @Test
    void actualizarConDatosInvalidosNoCambiaElProducto() {
        servicio.registrar("Café", "1.00", true);
        servicio.registrar("Té", "0.80", true);

        assertFalse(servicio.actualizar(1, "té", "1.00").isEmpty());
        assertFalse(servicio.actualizar(1, "Café", "gratis").isEmpty());
        assertEquals("Café", dao.productos.get(0).getNombre());
        assertEquals(new BigDecimal("1.00"), dao.productos.get(0).getPrecio());
    }

    @Test
    void alternarDisponibilidadDesdeLaLista() {
        servicio.registrar("Café", "1.00", true);

        assertFalse(servicio.alternarDisponibilidad(1).isDisponible());
        assertTrue(servicio.alternarDisponibilidad(1).isDisponible());
    }
}
