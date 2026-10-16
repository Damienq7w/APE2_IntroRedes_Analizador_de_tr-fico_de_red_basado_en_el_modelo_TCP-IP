package analizador.consulta;

import analizador.modelo.Paquete;

import java.util.ArrayList;
import java.util.List;

/**
 * Busquedas y filtros sobre los paquetes validos. No modifica la lista original:
 * siempre devuelve una lista nueva con los paquetes que cumplen la condicion.
 */
public class FiltroPaquetes {

    /** Paquetes cuya IP de origen o de destino coincide con la indicada. */
    public List<Paquete> porIp(List<Paquete> paquetes, String ip) {
        String buscada = ip.trim();
        List<Paquete> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            if (paquete.getIp().getIpOrigen().equals(buscada)
                    || paquete.getIp().getIpDestino().equals(buscada)) {
                resultado.add(paquete);
            }
        }
        return resultado;
    }

    /** Paquetes cuyo protocolo sobre IP es el indicado (TCP, UDP o ICMP). */
    public List<Paquete> porTransporte(List<Paquete> paquetes, String protocolo) {
        String buscado = protocolo.trim();
        List<Paquete> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            if (paquete.getProtocoloSobreIp().equalsIgnoreCase(buscado)) {
                resultado.add(paquete);
            }
        }
        return resultado;
    }

    /** Paquetes cuyo protocolo de aplicacion es el indicado (DNS, HTTP, SMTP...). */
    public List<Paquete> porAplicacion(List<Paquete> paquetes, String protocolo) {
        String buscado = protocolo.trim();
        List<Paquete> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            if (paquete.tieneAplicacion() && paquete.getProtocoloAplicacion().equalsIgnoreCase(buscado)) {
                resultado.add(paquete);
            }
        }
        return resultado;
    }
}
