package analizador.modelo;

import analizador.enums.Capa;
import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Map;

/**
 * Cabecera ICMP. En el archivo ocupa la posicion de transporte, pero ICMP es un protocolo
 * de control de la capa de Internet: viaja dentro de IP y no tiene puertos.
 */
public class CabeceraICMP extends CabeceraSobreIP {

    private static final String[] OBLIGATORIOS = {"type", "code", "id", "seq"};
    public static final int ECHO_REPLY = 0;
    public static final int ECHO_REQUEST = 8;

    public CabeceraICMP(Map<String, String> campos) {
        super("ICMP", campos);
    }

    @Override
    public Capa getCapa() {
        return Capa.INTERNET;
    }

    @Override
    protected String[] getCamposObligatorios() {
        return OBLIGATORIOS;
    }

    @Override
    protected void validarFormato() throws PaqueteMalFormadoException {
        exigirEnteroEnRango("type", 0, 255);
        exigirEnteroEnRango("code", 0, 255);
        exigirEnteroEnRango("id", 0, 65535);
        exigirEnteroEnRango("seq", 0, 65535);
    }

    public int getTipo() {
        return leerEntero("type");
    }

    public int getCodigo() {
        return leerEntero("code");
    }

    public int getIdentificador() {
        return leerEntero("id");
    }

    public int getSecuencia() {
        return leerEntero("seq");
    }

    public boolean esSolicitud() {
        return getTipo() == ECHO_REQUEST;
    }

    public boolean esRespuesta() {
        return getTipo() == ECHO_REPLY;
    }

    public String getDescripcionTipo() {
        switch (getTipo()) {
            case ECHO_REPLY:
                return "Echo Reply";
            case 3:
                return "Destino inalcanzable";
            case ECHO_REQUEST:
                return "Echo Request";
            case 11:
                return "Tiempo excedido";
            default:
                return "Tipo " + getTipo();
        }
    }
}
