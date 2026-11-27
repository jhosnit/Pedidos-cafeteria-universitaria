package ec.edu.epn.cafeteria.modelo;

import java.io.Serializable;

/**
 * Persona con cuenta en el sistema. Guarda lo comun a los tres roles.
 * El hash de la contrasena nunca se guarda en sesion (ver {@link #sinContrasena()}).
 *
 * @since 1.0
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasenaHash;
    private Rol rol;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    /**
     * @return nombre y apellido separados por un espacio
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Copia del usuario sin el hash, para dejarla en la sesion HTTP.
     *
     * @return nuevo objeto con los mismos datos y contrasenaHash en null
     */
    public Usuario sinContrasena() {
        Usuario copia = new Usuario();
        copia.id = id;
        copia.nombre = nombre;
        copia.apellido = apellido;
        copia.correo = correo;
        copia.rol = rol;
        return copia;
    }
}
