package analizador.excepciones;

import analizador.enums.TipoError;

/**
 * Se lanza cuando una linea del archivo no puede convertirse en un paquete valido.
 * Guarda el tipo de error, el motivo y, una vez conocida, la linea donde ocurrio,
 * para poder reportarla sin detener el programa.
 */
public class PaqueteMalFormadoException extends Exception {

    private static final long serialVersionUID = 1L;

    private final TipoError tipo;
    private final String motivo;
    private final int numeroLinea;
    private final String contenido;

    /** Error detectado al validar una cabecera, cuando todavia no se conoce la linea. */
    public PaqueteMalFormadoException(TipoError tipo, String motivo) {
        this(tipo, motivo, 0, "");
    }

    /** Error con toda la informacion de la linea que lo produjo. */
    public PaqueteMalFormadoException(TipoError tipo, String motivo, int numeroLinea, String contenido) {
        super("Linea " + numeroLinea + " [" + tipo.getDescripcion() + "]: " + motivo);
        this.tipo = tipo;
        this.motivo = motivo;
        this.numeroLinea = numeroLinea;
        this.contenido = contenido == null ? "" : contenido;
    }

    /** Crea una copia del error agregando el numero y el contenido de la linea. */
    public PaqueteMalFormadoException conLinea(int numeroLinea, String contenido) {
        return new PaqueteMalFormadoException(tipo, motivo, numeroLinea, contenido);
    }

    public TipoError getTipo() {
        return tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public String getContenido() {
        return contenido;
    }
}
