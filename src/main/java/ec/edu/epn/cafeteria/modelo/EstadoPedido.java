package ec.edu.epn.cafeteria.modelo;

/**
 * Etapas de atencion de un pedido. El orden de declaracion es el orden
 * en que avanza el pedido (RN-NEG-06): no hay saltos ni retrocesos.
 *
 * @since 1.0
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En preparación"),
    LISTO_PARA_RETIRAR("Listo para retirar"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * @return texto que se muestra al usuario
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Estado al que puede pasar el pedido desde este.
     *
     * @return el siguiente estado, o {@code null} si ya fue entregado
     */
    public EstadoPedido siguiente() {
        EstadoPedido[] todos = values();
        return ordinal() + 1 < todos.length ? todos[ordinal() + 1] : null;
    }

    /**
     * Nombre en minusculas, se usa como clase CSS de la etiqueta de estado.
     *
     * @return por ejemplo "en_preparacion"
     */
    public String getClaseCss() {
        return name().toLowerCase();
    }
}
