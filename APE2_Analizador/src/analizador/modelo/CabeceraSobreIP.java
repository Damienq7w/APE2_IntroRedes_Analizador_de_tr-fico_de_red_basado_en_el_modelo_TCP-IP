package analizador.modelo;

import java.util.Map;

/**
 * Cabecera de un protocolo que viaja dentro de la carga util de IP.
 * Es lo que indica el campo "proto" de la cabecera IP: TCP, UDP o ICMP.
 *
 * TCP y UDP pertenecen a la capa de transporte; ICMP ocupa la misma posicion en el
 * formato del archivo, pero es un protocolo de control de la capa de Internet.
 * Por eso esta clase agrupa a los tres sin afirmar que todos sean de transporte:
 * cada subclase declara su propia capa.
 */
public abstract class CabeceraSobreIP extends Cabecera {

    protected CabeceraSobreIP(String nombre, Map<String, String> campos) {
        super(nombre, campos);
    }

    /** Nombre del protocolo (TCP, UDP o ICMP); debe coincidir con el campo proto de IP. */
    public String getTipoProtocolo() {
        return getNombre();
    }
}
