package ec.edu.epn.cafeteria.dao;

import ec.edu.epn.cafeteria.modelo.Cliente;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.modelo.Rol;
import ec.edu.epn.cafeteria.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a las tablas usuario y cliente.
 *
 * @since 1.0
 */
public class UsuarioDao {

    /**
     * Busca una cuenta por correo sin distinguir mayusculas.
     *
     * @param correo correo con el que se inicia sesion
     * @return el usuario con su hash de contrasena, o {@code null} si no existe
     */
    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT id, nombre, apellido, correo, contrasena_hash, rol FROM usuario WHERE LOWER(correo) = LOWER(?)";
        try (Connection con = Conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasenaHash(rs.getString("contrasena_hash"));
                u.setRol(Rol.valueOf(rs.getString("rol")));
                return u;
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo buscar el usuario", e);
        }
    }

    /**
     * @param correo correo a revisar
     * @return true si alguna cuenta ya usa ese correo
     */
    public boolean existeCorreo(String correo) {
        return existe("SELECT 1 FROM usuario WHERE LOWER(correo) = LOWER(?)", correo);
    }

    /**
     * @param cedulaRuc cedula o RUC a revisar
     * @return true si algun cliente ya la registro
     */
    public boolean existeCedulaRuc(String cedulaRuc) {
        return existe("SELECT 1 FROM cliente WHERE cedula_ruc = ?", cedulaRuc);
    }

    /**
     * Guarda un cliente nuevo en una sola transaccion (usuario + cliente) y le
     * asigna su codigo con el formato CLI-00001.
     *
     * @param cliente datos del cliente con la contrasena ya cifrada
     * @throws ReglaNegocioException si otro registro gano la carrera con el mismo correo o cedula
     */
    public void registrarCliente(Cliente cliente) {
        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                int id = insertarUsuario(con, cliente);
                cliente.setId(id);
                cliente.setCodigoCliente(String.format("CLI-%05d", id));
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO cliente (usuario_id, codigo_cliente, cedula_ruc, celular) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setString(2, cliente.getCodigoCliente());
                    ps.setString(3, cliente.getCedulaRuc());
                    ps.setString(4, cliente.getCelular());
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if (DatosException.VIOLACION_UNICA.equals(e.getSQLState())) {
                throw new ReglaNegocioException("Ya existe una cuenta con esa cédula/RUC o ese correo.");
            }
            throw new DatosException("No se pudo registrar el cliente", e);
        }
    }

    /**
     * Guarda una cuenta con rol Personal (HU-03).
     *
     * @param personal datos del trabajador con la contrasena ya cifrada
     * @throws ReglaNegocioException si el correo ya fue registrado
     */
    public void registrarPersonal(Usuario personal) {
        personal.setRol(Rol.PERSONAL);
        try (Connection con = Conexion.obtener()) {
            personal.setId(insertarUsuario(con, personal));
        } catch (SQLException e) {
            if (DatosException.VIOLACION_UNICA.equals(e.getSQLState())) {
                throw new ReglaNegocioException("Ya existe una cuenta con ese correo.");
            }
            throw new DatosException("No se pudo registrar al personal", e);
        }
    }

    /**
     * @return cuentas con rol Personal ordenadas por apellido
     */
    public List<Usuario> listarPersonal() {
        String sql = "SELECT id, nombre, apellido, correo FROM usuario WHERE rol = 'PERSONAL' ORDER BY apellido, nombre";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(Rol.PERSONAL);
                lista.add(u);
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("No se pudo listar al personal", e);
        }
    }

    private int insertarUsuario(Connection con, Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, apellido, correo, contrasena_hash, rol) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasenaHash());
            ps.setString(5, u.getRol().name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private boolean existe(String sql, String valor) {
        try (Connection con = Conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo verificar el dato", e);
        }
    }
}
