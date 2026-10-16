package analizador.analisis;

import analizador.enums.TipoError;
import analizador.excepciones.PaqueteMalFormadoException;
import analizador.modelo.Paquete;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordina el analisis de todas las lineas. Cada linea se procesa de forma independiente
 * dentro de su propio try/catch, de modo que una linea mal formada se registra y el
 * programa continua con la siguiente.
 *
 * Ademas del analisis sintactico (ParserPaquete), verifica la coherencia semantica entre
 * cabeceras: una linea puede estar bien escrita y, aun asi, ser contradictoria.
 */
public class AnalizadorTrafico {

    private static final String TIPO_TRAMA_IPV4 = "IPv4";

    private final ParserPaquete parser;

    public AnalizadorTrafico() {
        this.parser = new ParserPaquete();
    }

    public ResultadoAnalisis procesar(List<String> lineas) {
        List<Paquete> validos = new ArrayList<>();
        List<PaqueteMalFormadoException> errores = new ArrayList<>();
        int vacias = 0;

        for (int i = 0; i < lineas.size(); i++) {
            int numeroLinea = i + 1;
            String linea = lineas.get(i);
            if (linea.trim().isEmpty()) {
                vacias++; // separadores del archivo: no son errores
                continue;
            }
            try {
                Paquete paquete = parser.parsear(numeroLinea, linea);
                validarCoherencia(paquete);
                validos.add(paquete);
            } catch (PaqueteMalFormadoException e) {
                errores.add(e.getNumeroLinea() == 0 ? e.conLinea(numeroLinea, linea) : e);
            } catch (RuntimeException e) {
                // Red de seguridad: ningun caso imprevisto debe cerrar el programa
                errores.add(new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "error inesperado al interpretar la linea: " + e, numeroLinea, linea));
            }
        }
        return new ResultadoAnalisis(validos, errores, lineas.size(), vacias);
    }

    /** Validacion semantica: las cabeceras deben ser coherentes entre si. */
    private void validarCoherencia(Paquete paquete) throws PaqueteMalFormadoException {
        String tipoTrama = paquete.getEthernet().getTipoTrama();
        if (!TIPO_TRAMA_IPV4.equalsIgnoreCase(tipoTrama)) {
            throw new PaqueteMalFormadoException(TipoError.SEMANTICO,
                    "ETH indica type=" + tipoTrama + ", pero la trama transporta una cabecera IP (se esperaba IPv4)");
        }
        String protocoloIp = paquete.getIp().getProtocolo();
        String protocoloSiguiente = paquete.getProtocoloSobreIp();
        if (!protocoloIp.equalsIgnoreCase(protocoloSiguiente)) {
            throw new PaqueteMalFormadoException(TipoError.SEMANTICO,
                    "IP indica proto=" + protocoloIp + ", pero la cabecera siguiente es " + protocoloSiguiente);
        }
    }
}
