package ec.edu.epn.cafeteria.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lista temporal de productos que arma el cliente antes de confirmar.
 * Vive en la sesion HTTP y no se guarda en la base (SUP-14).
 *
 * @since 1.0
 */
public class Seleccion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Cantidad maxima por producto (SUP-01). */
    public static final int CANTIDAD_MAXIMA = 10;

    // LinkedHashMap para que los productos se muestren en el orden en que se
    // agregaron
    private final Map<Integer, ItemSeleccion> items = new LinkedHashMap<>();

    /**
     * Agrega un producto. Si ya estaba, suma la cantidad (CA-05.5).
     *
     * @param producto producto del menu
     * @param cantidad cantidad a agregar, entre 1 y 10
     * @throws ReglaNegocioException si el producto no esta disponible, la cantidad
     *                               esta fuera de rango o la suma pasa de 10
     */
    public void agregar(Producto producto, int cantidad) {
        if (!producto.isDisponible()) {
            throw new ReglaNegocioException("El producto " + producto.getNombre() + " no está disponible.");
        }
        validarCantidad(cantidad);
        ItemSeleccion existente = items.get(producto.getId());
        if (existente == null) {
            items.put(producto.getId(),
                    new ItemSeleccion(producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad));
            return;
        }
        int nuevaCantidad = existente.getCantidad() + cantidad;
        if (nuevaCantidad > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioException("Puedes pedir máximo " + CANTIDAD_MAXIMA + " unidades de "
                    + producto.getNombre() + ". Ya tienes " + existente.getCantidad() + " en tu selección.");
        }
        existente.setCantidad(nuevaCantidad);
    }

    /**
     * Reemplaza la cantidad de un producto que ya esta en la seleccion (CA-05.6).
     *
     * @param productoId id del producto
     * @param cantidad   nueva cantidad, entre 1 y 10
     * @throws ReglaNegocioException si la cantidad esta fuera de rango
     */
    public void cambiarCantidad(int productoId, int cantidad) {
        validarCantidad(cantidad);
        ItemSeleccion item = items.get(productoId);
        if (item != null) {
            item.setCantidad(cantidad);
        }
    }

    /**
     * @param productoId id del producto que se quita de la seleccion
     */
    public void quitar(int productoId) {
        items.remove(productoId);
    }

    /**
     * @return copia de los items en orden de llegada
     */
    public List<ItemSeleccion> getItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Suma de precio por cantidad de cada item (RN-NEG-03). Vale 0.00 si esta
     * vacia.
     *
     * @return importe total con dos decimales
     */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (ItemSeleccion item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    /**
     * @return suma de las cantidades de todos los items
     */
    public int getCantidadProductos() {
        int suma = 0;
        for (ItemSeleccion item : items.values()) {
            suma += item.getCantidad();
        }
        return suma;
    }

    public boolean isVacia() {
        return items.isEmpty();
    }

    /** Se llama despues de confirmar el pedido (CA-07.8). */
    public void vaciar() {
        items.clear();
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad < 1 || cantidad > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioException("La cantidad debe ser un número entero entre 1 y " + CANTIDAD_MAXIMA + ".");
        }
    }
}
