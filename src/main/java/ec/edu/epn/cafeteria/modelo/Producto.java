package ec.edu.epn.cafeteria.modelo;

import java.math.BigDecimal;

/**
 * Articulo del menu (alimento o bebida). Los productos no se eliminan,
 * solo se marcan como no disponibles (SUP-10).
 *
 * @since 1.0
 */
public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private boolean disponible = true;

    public Producto() {
    }

    public Producto(int id, String nombre, BigDecimal precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
