package analizador.analisis;

import analizador.excepciones.PaqueteMalFormadoException;
import analizador.modelo.Paquete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de procesar el archivo: los paquetes validos, las lineas con error y
 * los contadores de lineas. Las listas que entrega son de solo lectura.
 */
public class ResultadoAnalisis {

    private final List<Paquete> validos;
    private final List<PaqueteMalFormadoException> errores;
    private final int lineasLeidas;
    private final int lineasVacias;

    public ResultadoAnalisis(List<Paquete> validos, List<PaqueteMalFormadoException> errores,
                             int lineasLeidas, int lineasVacias) {
        this.validos = Collections.unmodifiableList(new ArrayList<>(validos));
        this.errores = Collections.unmodifiableList(new ArrayList<>(errores));
        this.lineasLeidas = lineasLeidas;
        this.lineasVacias = lineasVacias;
    }

    public List<Paquete> getValidos() {
        return validos;
    }

    public List<PaqueteMalFormadoException> getErrores() {
        return errores;
    }

    public int getLineasLeidas() {
        return lineasLeidas;
    }

    public int getLineasVacias() {
        return lineasVacias;
    }

    /**
     * Paquete segun su numero de orden (1 = primer paquete valido).
     *
     * @return el paquete, o null si el numero esta fuera de rango
     */
    public Paquete getPaquete(int numero) {
        if (numero < 1 || numero > validos.size()) {
            return null;
        }
        return validos.get(numero - 1);
    }

    /** Numero de orden de un paquete (1..n), o -1 si no pertenece a este resultado. */
    public int getNumeroDe(Paquete paquete) {
        int indice = validos.indexOf(paquete);
        return indice < 0 ? -1 : indice + 1;
    }
}
