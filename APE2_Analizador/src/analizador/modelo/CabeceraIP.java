package analizador.modelo;

import analizador.analisis.ValidadorCampos;
import analizador.enums.Capa;
import analizador.enums.TipoError;
import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Map;

/**
 * Cabecera IP (capa de Internet). Sus direcciones identifican a los hosts extremos y se
 * conservan a lo largo de todo el recorrido, aunque la trama cambie en cada enlace.
 */
public class CabeceraIP extends Cabecera {

    private static final String[] OBLIGATORIOS = {"src", "dst", "ttl", "proto"};

    public CabeceraIP(Map<String, String> campos) {
        super("IP", campos);
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
        validarIp("src");
        validarIp("dst");
        // El TTL ocupa 8 bits en la cabecera IPv4
        exigirEnteroEnRango("ttl", 0, 255);
    }

    private void validarIp(String clave) throws PaqueteMalFormadoException {
        String ip = getCampo(clave);
        if (!ValidadorCampos.esIpv4Valida(ip)) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "direccion IPv4 invalida en IP." + clave + ": " + ip);
        }
    }

    public String getIpOrigen() {
        return getCampo("src");
    }

    public String getIpDestino() {
        return getCampo("dst");
    }

    /** Tiempo de vida: cada router lo reduce en 1 antes de reenviar el paquete. */
    public int getTtl() {
        return leerEntero("ttl");
    }

    /** Protocolo que transporta IP en su carga util (TCP, UDP o ICMP). */
    public String getProtocolo() {
        return getCampo("proto");
    }
}
