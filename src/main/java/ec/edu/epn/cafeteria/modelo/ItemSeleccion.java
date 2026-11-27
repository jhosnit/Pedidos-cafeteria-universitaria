package ec.edu.epn.cafeteria.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Producto con su cantidad dentro de la seleccion del cliente. Guarda el precio
 * que el cliente vio al agregarlo, para poder detectar si cambio al confirmar.
 *
 * @since 1.0
 */
public class ItemSeleccion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int productoId;
    private final String nombre;
    private BigDecimal precio;
    private int cantidad;

    public ItemSeleccion(int productoId, String nombre, BigDecimal precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getProductoId() { return productoId; }

    public String getNombre() { return nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /**
     * @return precio por cantidad
     */
    public BigDecimal getSubtotal() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}
