package ec.edu.epn.cafeteria.servicio;

import ec.edu.epn.cafeteria.dao.ProductoDao;
import ec.edu.epn.cafeteria.modelo.Producto;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Reglas del menu: registro, edicion y disponibilidad de productos (HU-04, HU-09 a HU-11).
 *
 * @since 1.0
 */
public class ProductoService {

    /** Largo maximo del nombre (SUP-11). */
    public static final int LARGO_MAXIMO_NOMBRE = 70;

    private final ProductoDao productoDao;

    public ProductoService() {
        this(new ProductoDao());
    }

    /**
     * @param productoDao dao a usar; en las pruebas se pasa uno falso
     */
    public ProductoService(ProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    /**
     * @return todos los productos, disponibles o no
     */
    public List<Producto> listar() {
        return productoDao.listar();
    }

    /**
     * @param id id del producto
     * @return el producto o {@code null}
     */
    public Producto buscar(int id) {
        return productoDao.buscarPorId(id);
    }

    /**
     * Registra un producto nuevo. Si no se indica lo contrario queda disponible (CA-09.1).
     *
     * @param nombre     nombre del producto
     * @param precioTexto precio tal como llega del formulario
     * @param disponible si se puede pedir desde ya
     * @return errores encontrados; vacia si se guardo
     */
    public List<String> registrar(String nombre, String precioTexto, boolean disponible) {
        Producto p = new Producto();
        List<String> errores = validar(nombre, precioTexto, 0, p);
        if (!errores.isEmpty()) {
            return errores;
        }
        p.setDisponible(disponible);
        try {
            productoDao.registrar(p);
        } catch (ReglaNegocioException e) {
            errores.add(e.getMessage());
        }
        return errores;
    }

    /**
     * Cambia nombre y precio de un producto existente (HU-10).
     *
     * @param id          id del producto
     * @param nombre      nombre nuevo
     * @param precioTexto precio nuevo tal como llega del formulario
     * @return errores encontrados; vacia si se guardo
     */
    public List<String> actualizar(int id, String nombre, String precioTexto) {
        Producto p = productoDao.buscarPorId(id);
        if (p == null) {
            List<String> errores = new ArrayList<>();
            errores.add("El producto no existe.");
            return errores;
        }
        List<String> errores = validar(nombre, precioTexto, id, p);
        if (!errores.isEmpty()) {
            return errores;
        }
        try {
            productoDao.actualizar(p);
        } catch (ReglaNegocioException e) {
            errores.add(e.getMessage());
        }
        return errores;
    }

    /**
     * Invierte la disponibilidad del producto desde la lista (CA-11.4).
     *
     * @param id id del producto
     * @return el producto con su nuevo estado, o {@code null} si no existe
     */
    public Producto alternarDisponibilidad(int id) {
        Producto p = productoDao.buscarPorId(id);
        if (p == null) {
            return null;
        }
        p.setDisponible(!p.isDisponible());
        productoDao.cambiarDisponibilidad(id, p.isDisponible());
        return p;
    }

    private List<String> validar(String nombre, String precioTexto, int idActual, Producto destino) {
        List<String> errores = new ArrayList<>();
        String nombreLimpio = nombre == null ? "" : nombre.trim();
        if (nombreLimpio.isEmpty()) {
            errores.add("El nombre es obligatorio.");
        } else if (nombreLimpio.length() > LARGO_MAXIMO_NOMBRE) {
            errores.add("El nombre puede tener máximo " + LARGO_MAXIMO_NOMBRE + " caracteres.");
        }
        BigDecimal precio = null;
        if (Validaciones.vacio(precioTexto)) {
            errores.add("El precio es obligatorio.");
        } else {
            precio = Validaciones.precio(precioTexto);
            if (precio == null) {
                errores.add("El precio debe ser un número mayor que 0 con máximo 2 decimales.");
            }
        }
        if (errores.isEmpty() && productoDao.existeNombre(nombreLimpio, idActual)) {
            errores.add("Ya existe un producto con ese nombre.");
        }
        if (errores.isEmpty()) {
            destino.setNombre(nombreLimpio);
            destino.setPrecio(precio);
        }
        return errores;
    }
}
