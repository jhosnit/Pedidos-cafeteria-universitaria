package ec.edu.epn.cafeteria.modelo;

import java.math.BigDecimal;

/**
 * Producto incluido en un pedido con el precio que tenia al confirmarse.
 *
 * @since 1.0
 */
public class DetallePedido {

    private int productoId;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioRegistrado;

    public DetallePedido() {
    }

    public DetallePedido(int productoId, String nombreProducto, int cantidad, BigDecimal precioRegistrado) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioRegistrado = precioRegistrado;
    }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioRegistrado() { return precioRegistrado; }
    public void setPrecioRegistrado(BigDecimal precioRegistrado) { this.precioRegistrado = precioRegistrado; }

    /**
     * @return precio registrado por cantidad
     */
    public BigDecimal getSubtotal() {
        return precioRegistrado.multiply(BigDecimal.valueOf(cantidad));
    }
}
