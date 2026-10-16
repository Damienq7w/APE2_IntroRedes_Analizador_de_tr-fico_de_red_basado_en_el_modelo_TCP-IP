package analizador.modelo;

import analizador.enums.Capa;

import java.util.Map;

/**
 * Cabecera de aplicacion: el protocolo de aplicacion y los datos que transporta.
 * Es la informacion que generan y consumen los procesos de los hosts extremos.
 */
public class CabeceraAplicacion extends Cabecera {

    private static final String[] OBLIGATORIOS = {"proto", "data"};

    public CabeceraAplicacion(Map<String, String> campos) {
        super("APP", campos);
    }

    @Override
    public Capa getCapa() {
        return Capa.APLICACION;
    }

    @Override
    protected String[] getCamposObligatorios() {
        return OBLIGATORIOS;
    }

    @Override
    protected void validarFormato() {
        // Los datos de aplicacion son texto libre: basta con que existan los campos obligatorios
    }

    public String getProtocolo() {
        return getCampo("proto");
    }

    public String getDatos() {
        return getCampo("data");
    }

    /** Indica si los datos comienzan con alguno de los prefijos dados (sin distinguir mayusculas). */
    public boolean datosEmpiezanCon(String... prefijos) {
        String datos = getDatos().toUpperCase();
        for (String prefijo : prefijos) {
            if (datos.startsWith(prefijo.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
