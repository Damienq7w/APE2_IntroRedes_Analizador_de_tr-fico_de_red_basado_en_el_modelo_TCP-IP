package analizador.modelo;

import analizador.enums.TipoError;
import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Cabecera TCP: transporte orientado a conexion, con numeros de secuencia y
 * confirmacion para ofrecer una entrega fiable y ordenada.
 */
public class CabeceraTCP extends CabeceraConPuertos {

    private static final String[] OBLIGATORIOS = {"sport", "dport", "seq", "ack", "flags"};
    private static final List<String> FLAGS_VALIDOS =
            Arrays.asList("SYN", "ACK", "PSH", "FIN", "RST", "URG");
    /** Los numeros de secuencia y ACK son de 32 bits sin signo: no caben en un int. */
    private static final long MAXIMO_32_BITS = 4294967295L;

    public CabeceraTCP(Map<String, String> campos) {
        super("TCP", campos);
    }

    @Override
    protected String[] getCamposObligatorios() {
        return OBLIGATORIOS;
    }

    @Override
    protected void validarCamposPropios() throws PaqueteMalFormadoException {
        exigirEnteroEnRango("seq", 0, MAXIMO_32_BITS);
        exigirEnteroEnRango("ack", 0, MAXIMO_32_BITS);
        for (String flag : getListaFlags()) {
            if (!FLAGS_VALIDOS.contains(flag)) {
                throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "flag TCP desconocido: '" + flag + "' (validos: " + FLAGS_VALIDOS + ")");
            }
        }
    }

    public long getSecuencia() {
        return leerLargo("seq");
    }

    public long getAck() {
        return leerLargo("ack");
    }

    /** Flags tal como aparecen en el archivo, por ejemplo "PSH-ACK". */
    public String getFlags() {
        return getCampo("flags");
    }

    /** Flags separados individualmente, por ejemplo [PSH, ACK]. */
    public List<String> getListaFlags() {
        String[] partes = getFlags().toUpperCase().split("-");
        return Collections.unmodifiableList(Arrays.asList(partes));
    }

    public boolean tieneFlag(String flag) {
        return getListaFlags().contains(flag.toUpperCase());
    }

    /** Un SYN sin ACK es el primer segmento del establecimiento de conexion. */
    public boolean esSyn() {
        return tieneFlag("SYN") && !tieneFlag("ACK");
    }
}
