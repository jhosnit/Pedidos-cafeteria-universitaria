package ec.edu.epn.cafeteria.modelo;

/**
 * Roles que puede tener una cuenta. Cada rol tiene su propia pantalla de inicio
 * despues de iniciar sesion (CA-02.1 a CA-02.3).
 *
 * @since 1.0
 */
public enum Rol {
    CLIENTE("/cliente/menu"),
    PERSONAL("/personal/pedidos"),
    ADMINISTRADOR("/admin/personal");

    private final String inicio;

    Rol(String inicio) {
        this.inicio = inicio;
    }

    /**
     * @return ruta relativa de la pantalla inicial del rol
     */
    public String getInicio() {
        return inicio;
    }
}
