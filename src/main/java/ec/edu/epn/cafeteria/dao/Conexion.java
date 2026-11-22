package ec.edu.epn.cafeteria.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexiones JDBC hacia PostgreSQL. Los datos de conexion se leen de las
 * variables de entorno DB_URL, DB_USER y DB_PASSWORD que define docker-compose.yml.
 * Si no existen se usan los valores para correr la base local en el puerto 5432.
 *
 * @since 1.0
 */
public final class Conexion {

    private static final String URL = valor("DB_URL", "jdbc:postgresql://localhost:5432/cafeteria");
    private static final String USUARIO = valor("DB_USER", "cafeteria");
    private static final String CLAVE = valor("DB_PASSWORD", "cafeteria_pass");

    static {
        try {
            // Dentro de Tomcat el driver no siempre se registra solo, por eso se carga a mano
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontro el driver de PostgreSQL", e);
        }
    }

    private Conexion() {
    }

    /**
     * Abre una conexion nueva. Quien la pide debe cerrarla (try-with-resources).
     *
     * @return conexion abierta con autocommit activado
     * @throws SQLException si la base no responde o las credenciales son incorrectas
     */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    private static String valor(String variable, String porDefecto) {
        String valor = System.getenv(variable);
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }
}
