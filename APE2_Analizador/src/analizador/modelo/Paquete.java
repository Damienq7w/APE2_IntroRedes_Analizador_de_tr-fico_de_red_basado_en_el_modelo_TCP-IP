package analizador.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Paquete de red ya desencapsulado. Se compone de sus cabeceras (composicion):
 * Ethernet e IP siempre estan presentes; la cabecera sobre IP y la de aplicacion
 * pueden faltar, porque la guia indica que cada paquete trae sus capas "segun corresponda".
 */
public class Paquete {

    private final int numeroLinea;
    private final CabeceraEthernet ethernet;
    private final CabeceraIP ip;
    private final CabeceraSobreIP sobreIp;
    private final CabeceraAplicacion aplicacion;

    public Paquete(int numeroLinea, CabeceraEthernet ethernet, CabeceraIP ip,
                   CabeceraSobreIP sobreIp, CabeceraAplicacion aplicacion) {
        this.numeroLinea = numeroLinea;
        this.ethernet = ethernet;
        this.ip = ip;
        this.sobreIp = sobreIp;
        this.aplicacion = aplicacion;
    }

    /** Linea del archivo de la que proviene el paquete. */
    public int getNumeroLinea() {
        return numeroLinea;
    }

    public CabeceraEthernet getEthernet() {
        return ethernet;
    }

    public CabeceraIP getIp() {
        return ip;
    }

    /** Cabecera TCP, UDP o ICMP; null si el paquete no la trae. */
    public CabeceraSobreIP getSobreIp() {
        return sobreIp;
    }

    /** Cabecera de aplicacion; null si el paquete no la trae. */
    public CabeceraAplicacion getAplicacion() {
        return aplicacion;
    }

    public boolean tieneSobreIp() {
        return sobreIp != null;
    }

    public boolean tieneAplicacion() {
        return aplicacion != null;
    }

    /** Nombre del protocolo sobre IP (TCP, UDP, ICMP) o "NINGUNO". */
    public String getProtocoloSobreIp() {
        return tieneSobreIp() ? sobreIp.getTipoProtocolo() : "NINGUNO";
    }

    /** Protocolo de aplicacion o "NINGUNO". */
    public String getProtocoloAplicacion() {
        return tieneAplicacion() ? aplicacion.getProtocolo() : "NINGUNO";
    }

    /** Cabeceras presentes, en el orden del encapsulamiento (de la capa inferior a la superior). */
    public List<Cabecera> getCabeceras() {
        List<Cabecera> lista = new ArrayList<>();
        lista.add(ethernet);
        lista.add(ip);
        if (tieneSobreIp()) {
            lista.add(sobreIp);
        }
        if (tieneAplicacion()) {
            lista.add(aplicacion);
        }
        return Collections.unmodifiableList(lista);
    }
}
