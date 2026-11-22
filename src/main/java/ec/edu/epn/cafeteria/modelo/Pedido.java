package ec.edu.epn.cafeteria.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido confirmado por un cliente.
 *
 * @since 1.0
 */
public class Pedido {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String codigoRetiro;
    private int clienteId;
    private String nombreCliente;
    private LocalDateTime fechaRegistro;
    private BigDecimal importeTotal;
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    private List<DetallePedido> detalles = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoRetiro() { return codigoRetiro; }
    public void setCodigoRetiro(String codigoRetiro) { this.codigoRetiro = codigoRetiro; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public BigDecimal getImporteTotal() { return importeTotal; }
    public void setImporteTotal(BigDecimal importeTotal) { this.importeTotal = importeTotal; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    /**
     * JSTL no sabe formatear LocalDateTime, por eso se expone ya como texto.
     *
     * @return fecha con formato dd/MM/yyyy HH:mm o cadena vacia
     */
    public String getFechaTexto() {
        return fechaRegistro == null ? "" : fechaRegistro.format(FORMATO_FECHA);
    }
}
