package analizador.analisis;

import analizador.enums.TipoError;
import analizador.excepciones.PaqueteMalFormadoException;
import analizador.modelo.Cabecera;
import analizador.modelo.CabeceraAplicacion;
import analizador.modelo.CabeceraEthernet;
import analizador.modelo.CabeceraICMP;
import analizador.modelo.CabeceraIP;
import analizador.modelo.CabeceraSobreIP;
import analizador.modelo.CabeceraTCP;
import analizador.modelo.CabeceraUDP;
import analizador.modelo.Paquete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convierte una linea de texto en un Paquete (analisis sintactico).
 *
 * Formato esperado:  ETH{...}|IP{...}|TCP/UDP/ICMP{...}|APP{...}
 * El bloque APP es opcional: la guia indica que cada paquete trae sus capas "segun corresponda".
 */
public class ParserPaquete {

    private static final String SEPARADOR_BLOQUES = "\\|";
    private static final int MINIMO_BLOQUES = 3;
    private static final int MAXIMO_BLOQUES = 4;

    /**
     * Interpreta una linea completa.
     *
     * @throws PaqueteMalFormadoException con el numero y el contenido de la linea si es invalida
     */
    public Paquete parsear(int numeroLinea, String linea) throws PaqueteMalFormadoException {
        try {
            return construirPaquete(numeroLinea, linea);
        } catch (PaqueteMalFormadoException e) {
            throw e.conLinea(numeroLinea, linea);
        }
    }

    private Paquete construirPaquete(int numeroLinea, String linea) throws PaqueteMalFormadoException {
        // El -1 conserva bloques vacios al final (por ejemplo "ETH{..}|IP{..}|") para reportarlos
        String[] bloques = linea.trim().split(SEPARADOR_BLOQUES, -1);
        if (bloques.length < MINIMO_BLOQUES || bloques.length > MAXIMO_BLOQUES) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "se esperaban entre " + MINIMO_BLOQUES + " y " + MAXIMO_BLOQUES
                            + " bloques separados por '|' y se encontraron " + bloques.length);
        }

        List<Cabecera> cabeceras = new ArrayList<>();
        Set<String> nombresVistos = new HashSet<>();
        for (String bloque : bloques) {
            Cabecera cabecera = interpretarBloque(bloque.trim());
            if (!nombresVistos.add(cabecera.getNombre())) {
                throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "bloque repetido: " + cabecera.getNombre());
            }
            cabeceras.add(cabecera);
        }

        verificarOrden(cabeceras);
        for (Cabecera cabecera : cabeceras) {
            cabecera.validar();
        }

        CabeceraEthernet ethernet = (CabeceraEthernet) cabeceras.get(0);
        CabeceraIP ip = (CabeceraIP) cabeceras.get(1);
        CabeceraSobreIP sobreIp = (CabeceraSobreIP) cabeceras.get(2);
        CabeceraAplicacion aplicacion =
                cabeceras.size() == MAXIMO_BLOQUES ? (CabeceraAplicacion) cabeceras.get(3) : null;
        return new Paquete(numeroLinea, ethernet, ip, sobreIp, aplicacion);
    }

    /** Separa un bloque NOMBRE{clave=valor,...} en su nombre y sus pares clave-valor. */
    private Cabecera interpretarBloque(String bloque) throws PaqueteMalFormadoException {
        int abre = bloque.indexOf('{');
        int cierra = bloque.lastIndexOf('}'); // hasta la ULTIMA llave, por seguridad
        if (abre <= 0) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "bloque sin nombre o sin llave de apertura '{': \"" + abreviar(bloque) + "\"");
        }
        String nombre = bloque.substring(0, abre).trim().toUpperCase();
        if (cierra < abre) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "falta la llave de cierre '}' en el bloque " + nombre);
        }
        if (cierra != bloque.length() - 1) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "texto inesperado despues de '}' en el bloque " + nombre);
        }
        String cuerpo = bloque.substring(abre + 1, cierra).trim();
        if (cuerpo.isEmpty()) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "el bloque " + nombre + " no tiene campos");
        }
        return crearCabecera(nombre, extraerCampos(nombre, cuerpo));
    }

    /** Extrae los pares clave=valor del cuerpo de un bloque, conservando su orden. */
    private Map<String, String> extraerCampos(String nombre, String cuerpo) throws PaqueteMalFormadoException {
        Map<String, String> campos = new LinkedHashMap<>();
        for (String par : cuerpo.split(",")) {
            // Limite 2: el valor puede contener '=' (por ejemplo data=GET_/search?q=redes)
            String[] claveValor = par.split("=", 2);
            String clave = claveValor[0].trim().toLowerCase();
            if (claveValor.length < 2 || clave.isEmpty()) {
                throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "par clave=valor invalido \"" + par.trim() + "\" en el bloque " + nombre);
            }
            if (campos.put(clave, claveValor[1].trim()) != null) {
                throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "campo repetido '" + clave + "' en el bloque " + nombre);
            }
        }
        return campos;
    }

    /** Crea la subclase de cabecera que corresponde al nombre del bloque. */
    private Cabecera crearCabecera(String nombre, Map<String, String> campos)
            throws PaqueteMalFormadoException {
        switch (nombre) {
            case "ETH":
                return new CabeceraEthernet(campos);
            case "IP":
                return new CabeceraIP(campos);
            case "TCP":
                return new CabeceraTCP(campos);
            case "UDP":
                return new CabeceraUDP(campos);
            case "ICMP":
                return new CabeceraICMP(campos);
            case "APP":
                return new CabeceraAplicacion(campos);
            default:
                throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                        "cabecera desconocida: " + nombre + " (se esperaba ETH, IP, TCP, UDP, ICMP o APP)");
        }
    }

    /** Comprueba que los bloques sigan el orden del encapsulamiento: ETH, IP, TCP/UDP/ICMP y APP. */
    private void verificarOrden(List<Cabecera> cabeceras) throws PaqueteMalFormadoException {
        exigirPosicion(cabeceras.get(0) instanceof CabeceraEthernet, 1, "ETH", cabeceras.get(0));
        exigirPosicion(cabeceras.get(1) instanceof CabeceraIP, 2, "IP", cabeceras.get(1));
        exigirPosicion(cabeceras.get(2) instanceof CabeceraSobreIP, 3, "TCP, UDP o ICMP", cabeceras.get(2));
        if (cabeceras.size() == MAXIMO_BLOQUES) {
            exigirPosicion(cabeceras.get(3) instanceof CabeceraAplicacion, 4, "APP", cabeceras.get(3));
        }
    }

    private void exigirPosicion(boolean correcta, int posicion, String esperado, Cabecera encontrada)
            throws PaqueteMalFormadoException {
        if (!correcta) {
            throw new PaqueteMalFormadoException(TipoError.SINTACTICO,
                    "el bloque " + posicion + " debe ser " + esperado + " y se encontro " + encontrada.getNombre());
        }
    }

    private String abreviar(String texto) {
        return texto.length() <= 40 ? texto : texto.substring(0, 40) + "...";
    }
}
