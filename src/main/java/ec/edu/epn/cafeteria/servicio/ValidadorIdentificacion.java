package ec.edu.epn.cafeteria.servicio;

/**
 * Valida cedulas y RUC ecuatorianos con el algoritmo del Registro Civil y del SRI (SUP-06).
 *
 * <p>Cedula: 10 digitos, provincia 01-24 o 30, tercer digito menor a 6 y digito
 * verificador por modulo 10. RUC: 13 digitos; persona natural (cedula + 001),
 * sociedad privada (tercer digito 9, modulo 11) o entidad publica (tercer digito 6, modulo 11).</p>
 *
 * @since 1.0
 */
public final class ValidadorIdentificacion {

    private static final int[] COEF_PRIVADA = {4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final int[] COEF_PUBLICA = {3, 2, 7, 6, 5, 4, 3, 2};

    private ValidadorIdentificacion() {
    }

    /**
     * @param valor texto ingresado por el usuario
     * @return true si es una cedula o un RUC valido
     */
    public static boolean esValida(String valor) {
        if (valor == null || !valor.matches("\\d{10}|\\d{13}")) {
            return false;
        }
        return valor.length() == 10 ? esCedula(valor) : esRuc(valor);
    }

    /**
     * @param cedula diez digitos
     * @return true si pasa provincia, tercer digito y modulo 10
     */
    public static boolean esCedula(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}") || !provinciaValida(cedula)) {
            return false;
        }
        int tercero = cedula.charAt(2) - '0';
        if (tercero >= 6) {
            return false;
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int d = (cedula.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += d > 9 ? d - 9 : d;
        }
        int verificador = (10 - suma % 10) % 10;
        return verificador == cedula.charAt(9) - '0';
    }

    private static boolean esRuc(String ruc) {
        if (!provinciaValida(ruc)) {
            return false;
        }
        int tercero = ruc.charAt(2) - '0';
        if (tercero < 6) {
            return esCedula(ruc.substring(0, 10)) && !ruc.endsWith("000");
        }
        if (tercero == 9) {
            return modulo11(ruc, COEF_PRIVADA) == ruc.charAt(9) - '0' && !ruc.endsWith("000");
        }
        if (tercero == 6) {
            return modulo11(ruc, COEF_PUBLICA) == ruc.charAt(8) - '0' && !ruc.endsWith("0000");
        }
        return false;
    }

    private static int modulo11(String ruc, int[] coeficientes) {
        int suma = 0;
        for (int i = 0; i < coeficientes.length; i++) {
            suma += (ruc.charAt(i) - '0') * coeficientes[i];
        }
        int residuo = suma % 11;
        return residuo == 0 ? 0 : 11 - residuo;
    }

    private static boolean provinciaValida(String valor) {
        int provincia = Integer.parseInt(valor.substring(0, 2));
        return (provincia >= 1 && provincia <= 24) || provincia == 30;
    }
}
