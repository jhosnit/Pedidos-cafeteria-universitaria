package ec.edu.epn.cafeteria.dao;

import ec.edu.epn.cafeteria.modelo.Producto;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla producto. No existe metodo para eliminar a proposito (SUP-10).
 *
 * @since 1.0
 */
public class ProductoDao {

    private static final String COLUMNAS = "SELECT id, codigo, nombre, precio, disponible FROM producto";

    /**
     * @return todos los productos ordenados por nombre
     */
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(COLUMNAS + " ORDER BY LOWER(nombre)");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leer(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("No se pudo listar los productos", e);
        }
    }

    /**
     * @param id id del producto
     * @return el producto o {@code null} si no existe
     */
    public Producto buscarPorId(int id) {
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo buscar el producto", e);
        }
    }

    /**
     * Revisa si el nombre ya lo usa otro producto, sin distinguir mayusculas.
     *
     * @param nombre    nombre a revisar
     * @param excluirId id del producto que se esta editando (0 si es nuevo)
     * @return true si otro producto ya tiene ese nombre
     */
    public boolean existeNombre(String nombre, int excluirId) {
        String sql = "SELECT 1 FROM producto WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        try (Connection con = Conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo verificar el nombre", e);
        }
    }

    /**
     * Inserta el producto y le asigna un codigo con formato PRD-0001.
     *
     * @param p producto con nombre, precio y disponibilidad validados
     */
    public void registrar(Producto p) {
        String sql = "INSERT INTO producto (nombre, precio, disponible) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setBigDecimal(2, p.getPrecio());
                ps.setBoolean(3, p.isDisponible());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    p.setId(rs.getInt(1));
                }
                p.setCodigo(String.format("PRD-%04d", p.getId()));
                try (PreparedStatement upd = con.prepareStatement("UPDATE producto SET codigo = ? WHERE id = ?")) {
                    upd.setString(1, p.getCodigo());
                    upd.setInt(2, p.getId());
                    upd.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw traducir(e, "No se pudo registrar el producto");
        }
    }

    /**
     * Cambia nombre y precio. Los pedidos ya confirmados no se tocan porque
     * guardan su propio precio en detalle_pedido (RN-NEG-04).
     *
     * @param p producto con id, nombre y precio nuevos
     */
    public void actualizar(Producto p) {
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement("UPDATE producto SET nombre = ?, precio = ? WHERE id = ?")) {
            ps.setString(1, p.getNombre());
            ps.setBigDecimal(2, p.getPrecio());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw traducir(e, "No se pudo actualizar el producto");
        }
    }

    /**
     * @param id         id del producto
     * @param disponible nuevo valor de disponibilidad
     */
    public void cambiarDisponibilidad(int id, boolean disponible) {
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement("UPDATE producto SET disponible = ? WHERE id = ?")) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("No se pudo cambiar la disponibilidad", e);
        }
    }

    private RuntimeException traducir(SQLException e, String mensaje) {
        // Si dos personas guardan el mismo nombre al mismo tiempo, el indice unico decide
        if (DatosException.VIOLACION_UNICA.equals(e.getSQLState())) {
            return new ReglaNegocioException("Ya existe un producto con ese nombre.");
        }
        return new DatosException(mensaje, e);
    }

    private Producto leer(ResultSet rs) throws SQLException {
        Producto p = new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getBigDecimal("precio"),
                rs.getBoolean("disponible"));
        p.setCodigo(rs.getString("codigo"));
        return p;
    }
}
