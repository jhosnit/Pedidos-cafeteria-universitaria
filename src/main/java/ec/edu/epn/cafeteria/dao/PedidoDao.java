package ec.edu.epn.cafeteria.dao;

import ec.edu.epn.cafeteria.modelo.DetallePedido;
import ec.edu.epn.cafeteria.modelo.EstadoPedido;
import ec.edu.epn.cafeteria.modelo.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a las tablas pedido y detalle_pedido.
 *
 * @since 1.0
 */
public class PedidoDao {

    private static final String SELECT_PEDIDO = "SELECT p.id, p.codigo_retiro, p.cliente_id, p.fecha_registro, p.importe_total, p.estado, "
            + "u.nombre || ' ' || u.apellido AS nombre_cliente "
            + "FROM pedido p JOIN usuario u ON u.id = p.cliente_id";

    /**
     * Guarda el pedido y sus detalles en una sola transaccion (RNF-06). Si algo
     * falla se hace rollback y no queda nada a medias.
     *
     * @param pedido pedido con codigo, cliente, importe y detalles
     * @return true si se guardo; false si el codigo de retiro ya existia y hay que
     *         generar otro
     */
    public boolean insertar(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedido (codigo_retiro, cliente_id, importe_total, estado) "
                + "VALUES (?, ?, ?, ?) RETURNING id, fecha_registro";
        String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_registrado) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setString(1, pedido.getCodigoRetiro());
                    ps.setInt(2, pedido.getClienteId());
                    ps.setBigDecimal(3, pedido.getImporteTotal());
                    ps.setString(4, pedido.getEstado().name());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        pedido.setId(rs.getInt("id"));
                        pedido.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    for (DetallePedido d : pedido.getDetalles()) {
                        ps.setInt(1, pedido.getId());
                        ps.setInt(2, d.getProductoId());
                        ps.setInt(3, d.getCantidad());
                        ps.setBigDecimal(4, d.getPrecioRegistrado());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                if (DatosException.VIOLACION_UNICA.equals(e.getSQLState())
                        && e.getMessage() != null && e.getMessage().contains("uq_pedido_codigo_retiro")) {
                    return false;
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo registrar el pedido", e);
        }
    }

    /**
     * Pedidos de un cliente, del mas reciente al mas antiguo (CA-08.1).
     *
     * @param clienteId id del cliente en sesion
     * @return lista sin detalles
     */
    public List<Pedido> listarPorCliente(int clienteId) {
        return listar(SELECT_PEDIDO + " WHERE p.cliente_id = ? ORDER BY p.fecha_registro DESC, p.id DESC", clienteId);
    }

    /**
     * @param id id del pedido
     * @return el pedido con sus detalles, o {@code null} si no existe
     */
    public Pedido buscarPorId(int id) {
        List<Pedido> encontrados = listar(SELECT_PEDIDO + " WHERE p.id = ?", id);
        return encontrados.isEmpty() ? null : conDetalles(encontrados.get(0));
    }

    private List<Pedido> listar(String sql, Object... parametros) {
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("id"));
                    p.setCodigoRetiro(rs.getString("codigo_retiro"));
                    p.setClienteId(rs.getInt("cliente_id"));
                    p.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
                    p.setImporteTotal(rs.getBigDecimal("importe_total"));
                    p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                    p.setNombreCliente(rs.getString("nombre_cliente"));
                    lista.add(p);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("No se pudo consultar los pedidos", e);
        }
    }

    private Pedido conDetalles(Pedido pedido) {
        String sql = "SELECT d.producto_id, pr.nombre, d.cantidad, d.precio_registrado "
                + "FROM detalle_pedido d JOIN producto pr ON pr.id = d.producto_id "
                + "WHERE d.pedido_id = ? ORDER BY d.id";
        try (Connection con = Conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedido.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedido.getDetalles().add(new DetallePedido(rs.getInt("producto_id"), rs.getString("nombre"),
                            rs.getInt("cantidad"), rs.getBigDecimal("precio_registrado")));
                }
            }
            return pedido;
        } catch (SQLException e) {
            throw new DatosException("No se pudo consultar el detalle del pedido", e);
        }
    }
}
