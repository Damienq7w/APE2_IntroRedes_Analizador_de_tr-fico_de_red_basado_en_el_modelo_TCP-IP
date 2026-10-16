package analizador.modelo;

import analizador.analisis.ValidadorCampos;
import analizador.enums.Capa;
import analizador.enums.TipoError;
import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Map;

/**
 * Cabecera Ethernet (capa de acceso a red). Sus direcciones MAC solo tienen alcance local:
 * identifican interfaces dentro del enlace actual y cambian en cada salto.
 */
public class CabeceraEthernet extends Cabecera {

    private static final String[] OBLIGATORIOS = {"src", "dst", "type"};

    public CabeceraEthernet(Map<String, String> campos) {
        super("ETH", campos);
    }

    @Override
    public Capa getCapa() {
        return Capa.ACCESO_RED;
    }

    @Override
    protected String[] getCamposObligatorios() {
        return OBLIGATORIOS;
    }

    @Override
    protected void validarFormato() throws PaqueteMalFormadoException {
        validarMac("src");
        validarMac("dst");
    }

    private void validarMac(String clave) throws PaqueteMalFormadoException {
        String mac = getCampo(clave);
        if (!ValidadorCampos.esMacValida(mac)) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "direccion MAC invalida en ETH." + clave + ": " + mac);
        }
    }

    public String getMacOrigen() {
        return getCampo("src");
    }

    public String getMacDestino() {
        return getCampo("dst");
    }

    /** Tipo de trama: indica que protocolo transporta la trama (por ejemplo IPv4). */
    public String getTipoTrama() {
        return getCampo("type");
    }
}
