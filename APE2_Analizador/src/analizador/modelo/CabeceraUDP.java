package analizador.modelo;

import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Map;

/**
 * Cabecera UDP: transporte sin conexion y sin garantia de entrega.
 * Mas simple que TCP; la usan servicios de consulta y respuesta como DNS, NTP o SNMP.
 */
public class CabeceraUDP extends CabeceraConPuertos {

    private static final String[] OBLIGATORIOS = {"sport", "dport", "length"};

    public CabeceraUDP(Map<String, String> campos) {
        super("UDP", campos);
    }

    @Override
    protected String[] getCamposObligatorios() {
        return OBLIGATORIOS;
    }

    @Override
    protected void validarCamposPropios() throws PaqueteMalFormadoException {
        // La longitud incluye los 8 bytes de la propia cabecera UDP y ocupa 16 bits
        exigirEnteroEnRango("length", 8, 65535);
    }

    /** Longitud del datagrama UDP en bytes (cabecera + datos). */
    public int getLongitud() {
        return leerEntero("length");
    }
}
