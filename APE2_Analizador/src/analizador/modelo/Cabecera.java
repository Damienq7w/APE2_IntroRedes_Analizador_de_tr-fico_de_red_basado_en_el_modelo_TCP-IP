package analizador.modelo;

import analizador.analisis.ValidadorCampos;
import analizador.enums.Capa;
import analizador.enums.TipoError;
import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cabecera generica de un paquete: un nombre (ETH, IP, TCP...) y sus campos clave=valor.
 * Es la base de la jerarquia; cada subclase representa la cabecera de un protocolo concreto.
 *
 * Aplica el patron plantilla: validar() siempre comprueba primero los campos obligatorios
 * (logica comun escrita una sola vez) y despues el formato propio de cada protocolo.
 */
public abstract class Cabecera {

    private final String nombre;
    private final Map<String, String> campos;

    protected Cabecera(String nombre, Map<String, String> campos) {
        this.nombre = nombre;
        // Copia defensiva: el LinkedHashMap conserva el orden original de los campos
        this.campos = Collections.unmodifiableMap(new LinkedHashMap<>(campos));
    }

    /** Capa del modelo TCP/IP a la que pertenece la cabecera. */
    public abstract Capa getCapa();

    /** Campos que deben estar presentes para que la cabecera sea valida. */
    protected abstract String[] getCamposObligatorios();

    /** Reglas de formato especificas del protocolo (rangos, direcciones, etc.). */
    protected abstract void validarFormato() throws PaqueteMalFormadoException;

    /** Valida la cabecera completa: primero los obligatorios y luego el formato. */
    public final void validar() throws PaqueteMalFormadoException {
        validarCamposObligatorios();
        validarFormato();
    }

    private void validarCamposObligatorios() throws PaqueteMalFormadoException {
        for (String clave : getCamposObligatorios()) {
            String valor = campos.get(clave);
            if (valor == null || valor.isEmpty()) {
                throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "falta el campo obligatorio '" + clave + "' en la cabecera " + nombre);
            }
        }
    }

    /** Comprueba que un campo sea un entero dentro del rango indicado. */
    protected void exigirEnteroEnRango(String clave, long minimo, long maximo)
            throws PaqueteMalFormadoException {
        String valor = campos.get(clave);
        if (!ValidadorCampos.esEnteroEnRango(valor, minimo, maximo)) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "el campo '" + clave + "' de " + nombre + " debe ser un entero entre "
                            + minimo + " y " + maximo + " (valor recibido: " + valor + ")");
        }
    }

    protected int leerEntero(String clave) {
        return Integer.parseInt(campos.get(clave));
    }

    protected long leerLargo(String clave) {
        return Long.parseLong(campos.get(clave));
    }

    public String getNombre() {
        return nombre;
    }

    /** Valor de un campo, o null si la cabecera no lo contiene. */
    public String getCampo(String clave) {
        return campos.get(clave);
    }

    public boolean tieneCampo(String clave) {
        return campos.containsKey(clave);
    }

    /** Todos los campos en su orden original; el mapa es de solo lectura. */
    public Map<String, String> getCampos() {
        return campos;
    }

    @Override
    public String toString() {
        return nombre + campos;
    }
}
