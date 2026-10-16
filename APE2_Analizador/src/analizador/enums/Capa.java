package analizador.enums;

/**
 * Capas del modelo TCP/IP de cuatro niveles que utiliza la guia de la practica.
 * Cada cabecera del paquete declara a que capa pertenece.
 */
public enum Capa {

    ACCESO_RED("Capa de acceso a red"),
    INTERNET("Capa de Internet"),
    TRANSPORTE("Capa de transporte"),
    APLICACION("Capa de aplicacion");

    private final String descripcion;

    Capa(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Texto en mayusculas usado como encabezado en los reportes, por ejemplo "CAPA DE INTERNET". */
    public String getEtiqueta() {
        return descripcion.toUpperCase();
    }
}
