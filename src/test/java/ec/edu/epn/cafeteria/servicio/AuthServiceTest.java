package ec.edu.epn.cafeteria.servicio;

import ec.edu.epn.cafeteria.dao.UsuarioDao;
import ec.edu.epn.cafeteria.modelo.Cliente;
import ec.edu.epn.cafeteria.modelo.Rol;
import ec.edu.epn.cafeteria.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de registro e inicio de sesion (HU-01, HU-02, HU-03) sin base de datos:
 * se usa un UsuarioDao en memoria.
 */
class AuthServiceTest {

    /** Guarda los usuarios en una lista en lugar de PostgreSQL. */
    static class UsuarioDaoEnMemoria extends UsuarioDao {
        final List<Usuario> usuarios = new ArrayList<>();

        @Override
        public Usuario buscarPorCorreo(String correo) {
            return usuarios.stream().filter(u -> u.getCorreo().equalsIgnoreCase(correo)).findFirst().orElse(null);
        }

        @Override
        public boolean existeCorreo(String correo) {
            return buscarPorCorreo(correo) != null;
        }

        @Override
        public boolean existeCedulaRuc(String cedulaRuc) {
            return usuarios.stream().anyMatch(u -> u instanceof Cliente c && c.getCedulaRuc().equals(cedulaRuc));
        }

        @Override
        public void registrarCliente(Cliente cliente) {
            cliente.setId(usuarios.size() + 1);
            cliente.setCodigoCliente(String.format("CLI-%05d", cliente.getId()));
            usuarios.add(cliente);
        }

        @Override
        public void registrarPersonal(Usuario personal) {
            personal.setRol(Rol.PERSONAL);
            personal.setId(usuarios.size() + 1);
            usuarios.add(personal);
        }
    }

    private UsuarioDaoEnMemoria dao;
    private AuthService servicio;

    @BeforeEach
    void preparar() {
        dao = new UsuarioDaoEnMemoria();
        servicio = new AuthService(dao);
    }

    private Cliente clienteValido() {
        Cliente c = new Cliente();
        c.setNombre("Ana");
        c.setApellido("Torres");
        c.setCedulaRuc("1710034065");
        c.setCelular("0991234567");
        c.setCorreo("ana.torres@epn.edu.ec");
        return c;
    }

    @Test
    void registroExitosoGuardaHashYAsignaCodigo() {
        List<String> errores = servicio.registrarCliente(clienteValido(), "clave1234", "clave1234");

        assertTrue(errores.isEmpty());
        Cliente guardado = (Cliente) dao.usuarios.get(0);
        assertEquals("CLI-00001", guardado.getCodigoCliente());
        assertNotEquals("clave1234", guardado.getContrasenaHash());
        assertTrue(guardado.getContrasenaHash().startsWith("$2a$"));
    }

    @Test
    void camposVaciosSeInformanYNoSeCreaLaCuenta() {
        Cliente c = new Cliente();
        List<String> errores = servicio.registrarCliente(c, "", "");

        assertEquals(7, errores.size());
        assertTrue(dao.usuarios.isEmpty());
    }

    @Test
    void correoFueraDelDominioInstitucionalSeRechaza() {
        Cliente c = clienteValido();
        c.setCorreo("ana@gmail.com");

        List<String> errores = servicio.registrarCliente(c, "clave1234", "clave1234");

        assertTrue(errores.contains("El correo debe ser institucional (@epn.edu.ec)."));
    }

    @Test
    void celularConMenosDeDiezDigitosSeRechaza() {
        Cliente c = clienteValido();
        c.setCelular("09912345");

        assertFalse(servicio.registrarCliente(c, "clave1234", "clave1234").isEmpty());
    }

    @Test
    void contrasenaCortaOConfirmacionDistintaSeRechaza() {
        assertTrue(servicio.registrarCliente(clienteValido(), "corta", "corta")
                .contains("La contraseña debe tener al menos 8 caracteres."));
        assertTrue(servicio.registrarCliente(clienteValido(), "clave1234", "clave9999")
                .contains("La contraseña y su confirmación no coinciden."));
    }

    @Test
    void cedulaOCorreoRepetidoNoCreaOtraCuenta() {
        servicio.registrarCliente(clienteValido(), "clave1234", "clave1234");

        List<String> errores = servicio.registrarCliente(clienteValido(), "clave1234", "clave1234");

        assertTrue(errores.contains("Ya existe una cuenta con esa cédula o RUC."));
        assertTrue(errores.contains("Ya existe una cuenta con ese correo."));
        assertEquals(1, dao.usuarios.size());
    }

    @Test
    void loginCorrectoDevuelveUsuarioSinHash() {
        servicio.registrarCliente(clienteValido(), "clave1234", "clave1234");

        Usuario u = servicio.autenticar("ANA.TORRES@epn.edu.ec", "clave1234");

        assertNotNull(u);
        assertEquals(Rol.CLIENTE, u.getRol());
        assertNull(u.getContrasenaHash());
    }

    @Test
    void loginConCredencialesIncorrectasDevuelveNull() {
        servicio.registrarCliente(clienteValido(), "clave1234", "clave1234");

        assertNull(servicio.autenticar("ana.torres@epn.edu.ec", "otraclave"));
        assertNull(servicio.autenticar("nadie@epn.edu.ec", "clave1234"));
        assertNull(servicio.autenticar("", ""));
    }

    @Test
    void administradorRegistraPersonalQuePuedeIniciarSesion() {
        Usuario p = new Usuario();
        p.setNombre("Carlos");
        p.setApellido("Mena");
        p.setCorreo("carlos.mena@epn.edu.ec");

        assertTrue(servicio.registrarPersonal(p, "personal123", "personal123").isEmpty());
        Usuario sesion = servicio.autenticar("carlos.mena@epn.edu.ec", "personal123");
        assertEquals(Rol.PERSONAL, sesion.getRol());
    }
}
