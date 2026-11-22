package ec.edu.epn.cafeteria.modelo;

/**
 * Estudiante o docente que hace pedidos. Se registra a si mismo (HU-01).
 *
 * @since 1.0
 */
public class Cliente extends Usuario {

    private static final long serialVersionUID = 1L;

    private String codigoCliente;
    private String cedulaRuc;
    private String celular;

    public Cliente() {
        setRol(Rol.CLIENTE);
    }

    public String getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(String codigoCliente) { this.codigoCliente = codigoCliente; }

    public String getCedulaRuc() { return cedulaRuc; }
    public void setCedulaRuc(String cedulaRuc) { this.cedulaRuc = cedulaRuc; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
}
