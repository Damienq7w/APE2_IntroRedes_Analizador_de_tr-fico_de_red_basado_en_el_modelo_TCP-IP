package analizador.enums;

/**
 * Clasifica los errores de una linea segun las dimensiones de un protocolo vistas en clase.
 * SINTACTICO: la forma de la linea o de un campo es invalida (llaves, separadores, formato, rango).
 * SEMANTICO: la linea esta bien escrita, pero sus cabeceras no son coherentes entre si.
 */
public enum TipoError {

    SINTACTICO("Sintactico"),
    SEMANTICO("Semantico");

    private final String descripcion;

    TipoError(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
