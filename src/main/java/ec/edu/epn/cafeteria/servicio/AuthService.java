package ec.edu.epn.cafeteria.servicio;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ec.edu.epn.cafeteria.dao.UsuarioDao;
import ec.edu.epn.cafeteria.modelo.Cliente;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro de cuentas e inicio de sesion (HU-01, HU-02, HU-03).
 *
 * @since 1.0
 */
public class AuthService {

    private static final int COSTO_BCRYPT = 10;

    private final UsuarioDao usuarioDao;

    public AuthService() {
        this(new UsuarioDao());
    }

    /**
     * @param usuarioDao dao a usar; en las pruebas se pasa uno falso
     */
    public AuthService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    /**
     * Valida los datos y crea la cuenta del cliente.
     *
     * @param c            datos del formulario (sin contrasena)
     * @param contrasena   contrasena en texto plano
     * @param confirmacion repeticion de la contrasena
     * @return lista de errores para mostrar; vacia si la cuenta se creo
     */
    public List<String> registrarCliente(Cliente c, String contrasena, String confirmacion) {
        List<String> errores = new ArrayList<>();
        if (Validaciones.vacio(c.getNombre())) errores.add("El nombre es obligatorio.");
        if (Validaciones.vacio(c.getApellido())) errores.add("El apellido es obligatorio.");
        if (Validaciones.vacio(c.getCedulaRuc())) errores.add("La cédula o RUC es obligatoria.");
        if (Validaciones.vacio(c.getCelular())) errores.add("El celular es obligatorio.");
        if (Validaciones.vacio(c.getCorreo())) errores.add("El correo es obligatorio.");
        if (Validaciones.vacio(contrasena)) errores.add("La contraseña es obligatoria.");
        if (Validaciones.vacio(confirmacion)) errores.add("Debes confirmar la contraseña.");
        if (!errores.isEmpty()) {
            return errores;
        }

        if (!ValidadorIdentificacion.esValida(c.getCedulaRuc())) {
            errores.add("La cédula o RUC no es válida.");
        }
        if (!Validaciones.celular(c.getCelular())) {
            errores.add("El celular debe tener 10 dígitos numéricos.");
        }
        validarCorreoYContrasena(c.getCorreo(), contrasena, confirmacion, errores);
        if (!errores.isEmpty()) {
            return errores;
        }

        if (usuarioDao.existeCedulaRuc(c.getCedulaRuc())) {
            errores.add("Ya existe una cuenta con esa cédula o RUC.");
        }
        if (usuarioDao.existeCorreo(c.getCorreo())) {
            errores.add("Ya existe una cuenta con ese correo.");
        }
        if (!errores.isEmpty()) {
            return errores;
        }

        c.setContrasenaHash(cifrar(contrasena));
        try {
            usuarioDao.registrarCliente(c);
        } catch (ReglaNegocioException e) {
            errores.add(e.getMessage());
        }
        return errores;
    }

    /**
     * Crea una cuenta con rol Personal. Solo la usa el administrador.
     *
     * @param p            nombre, apellido y correo del trabajador
     * @param contrasena   contrasena en texto plano
     * @param confirmacion repeticion de la contrasena
     * @return lista de errores para mostrar; vacia si la cuenta se creo
     */
    public List<String> registrarPersonal(Usuario p, String contrasena, String confirmacion) {
        List<String> errores = new ArrayList<>();
        if (Validaciones.vacio(p.getNombre())) errores.add("El nombre es obligatorio.");
        if (Validaciones.vacio(p.getApellido())) errores.add("El apellido es obligatorio.");
        if (Validaciones.vacio(p.getCorreo())) errores.add("El correo es obligatorio.");
        if (Validaciones.vacio(contrasena)) errores.add("La contraseña es obligatoria.");
        if (Validaciones.vacio(confirmacion)) errores.add("Debes confirmar la contraseña.");
        if (!errores.isEmpty()) {
            return errores;
        }
        validarCorreoYContrasena(p.getCorreo(), contrasena, confirmacion, errores);
        if (errores.isEmpty() && usuarioDao.existeCorreo(p.getCorreo())) {
            errores.add("Ya existe una cuenta con ese correo.");
        }
        if (!errores.isEmpty()) {
            return errores;
        }
        p.setContrasenaHash(cifrar(contrasena));
        try {
            usuarioDao.registrarPersonal(p);
        } catch (ReglaNegocioException e) {
            errores.add(e.getMessage());
        }
        return errores;
    }

    /**
     * Comprueba correo y contrasena. No dice cual de los dos fallo (CA-02.4).
     *
     * @param correo     correo ingresado
     * @param contrasena contrasena ingresada
     * @return el usuario sin hash si las credenciales son correctas, o {@code null}
     */
    public Usuario autenticar(String correo, String contrasena) {
        if (Validaciones.vacio(correo) || Validaciones.vacio(contrasena)) {
            return null;
        }
        Usuario usuario = usuarioDao.buscarPorCorreo(correo.trim());
        if (usuario == null) {
            return null;
        }
        boolean coincide = BCrypt.verifyer()
                .verify(contrasena.toCharArray(), usuario.getContrasenaHash()).verified;
        return coincide ? usuario.sinContrasena() : null;
    }

    /**
     * @return lista con los trabajadores registrados
     */
    public List<Usuario> listarPersonal() {
        return usuarioDao.listarPersonal();
    }

    /**
     * @param contrasena texto plano
     * @return hash BCrypt con sal (RNF-02)
     */
    static String cifrar(String contrasena) {
        return BCrypt.withDefaults().hashToString(COSTO_BCRYPT, contrasena.toCharArray());
    }

    private void validarCorreoYContrasena(String correo, String contrasena, String confirmacion, List<String> errores) {
        if (!Validaciones.correoInstitucional(correo)) {
            errores.add("El correo debe ser institucional (" + Validaciones.DOMINIO + ").");
        }
        if (!Validaciones.contrasena(contrasena)) {
            errores.add("La contraseña debe tener al menos 8 caracteres.");
        } else if (!contrasena.equals(confirmacion)) {
            errores.add("La contraseña y su confirmación no coinciden.");
        }
    }
}
