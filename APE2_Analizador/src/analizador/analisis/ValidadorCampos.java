package analizador.analisis;

import java.util.regex.Pattern;

/**
 * Reglas de formato de los campos, escritas una sola vez y reutilizadas por todas las
 * cabeceras. Usa java.util.regex, que forma parte de Java (no es una libreria externa).
 */
public final class ValidadorCampos {

    /** Seis pares de digitos hexadecimales separados por dos puntos, por ejemplo AA:BB:CC:DD:EE:01. */
    private static final Pattern MAC = Pattern.compile("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$");
    /** Cuatro grupos de 1 a 3 digitos separados por puntos; el rango se verifica aparte. */
    private static final Pattern IPV4 = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");
    /** Entero sin signo, sin espacios ni simbolos. */
    private static final Pattern ENTERO = Pattern.compile("^\\d{1,19}$");

    private ValidadorCampos() {
        // Clase de utilidad: no se instancia
    }

    public static boolean esMacValida(String texto) {
        return texto != null && MAC.matcher(texto).matches();
    }

    /** IPv4 valida: cuatro octetos, cada uno entre 0 y 255. */
    public static boolean esIpv4Valida(String texto) {
        if (texto == null || !IPV4.matcher(texto).matches()) {
            return false;
        }
        for (String octeto : texto.split("\\.")) {
            if (Integer.parseInt(octeto) > 255) {
                return false;
            }
        }
        return true;
    }

    public static boolean esEntero(String texto) {
        return texto != null && ENTERO.matcher(texto).matches();
    }

    /** Entero no negativo dentro del rango [minimo, maximo]. */
    public static boolean esEnteroEnRango(String texto, long minimo, long maximo) {
        if (!esEntero(texto)) {
            return false;
        }
        try {
            long valor = Long.parseLong(texto);
            return valor >= minimo && valor <= maximo;
        } catch (NumberFormatException e) {
            return false; // demasiado grande incluso para un long
        }
    }

    public static boolean esPuertoValido(int puerto) {
        return puerto >= 0 && puerto <= 65535;
    }

    public static boolean esTtlValido(int ttl) {
        return ttl >= 0 && ttl <= 255;
    }
}
