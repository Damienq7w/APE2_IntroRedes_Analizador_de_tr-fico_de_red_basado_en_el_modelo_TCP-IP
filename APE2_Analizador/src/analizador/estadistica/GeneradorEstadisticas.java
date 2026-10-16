package analizador.estadistica;

import analizador.analisis.ResultadoAnalisis;
import analizador.enums.ServicioConocido;
import analizador.modelo.CabeceraAplicacion;
import analizador.modelo.CabeceraConPuertos;
import analizador.modelo.CabeceraICMP;
import analizador.modelo.CabeceraSobreIP;
import analizador.modelo.CabeceraTCP;
import analizador.modelo.Paquete;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcula el resumen estadistico a partir de los paquetes validos.
 * Usa mapas (clave -> cantidad) para todos los conteos.
 */
public class GeneradorEstadisticas {

    private static final String SIN_PUERTO_CONOCIDO = "Puerto no registrado";
    private static final String SIN_PUERTOS_ICMP = "ICMP (sin puertos)";

    public ResumenEstadistico generar(ResultadoAnalisis resultado) {
        List<Paquete> paquetes = resultado.getValidos();

        Map<String, Integer> porProtocoloSobreIp = new LinkedHashMap<>();
        Map<String, Integer> porAplicacion = new LinkedHashMap<>();
        Map<String, Integer> porServicio = new LinkedHashMap<>();
        Map<String, Integer> ipsOrigen = new LinkedHashMap<>();
        Map<String, Integer> ipsDestino = new LinkedHashMap<>();
        Map<String, Integer> puertosDestino = new LinkedHashMap<>();
        Map<String, Integer> flagsPorCombinacion = new LinkedHashMap<>();
        Map<String, Integer> flagsPorBit = new LinkedHashMap<>();
        Map<String, Integer> consultas = new LinkedHashMap<>();
        Map<String, Integer> respuestas = new LinkedHashMap<>();
        int paquetesConPuertos = 0;

        for (Paquete paquete : paquetes) {
            incrementar(ipsOrigen, paquete.getIp().getIpOrigen());
            incrementar(ipsDestino, paquete.getIp().getIpDestino());
            incrementar(porProtocoloSobreIp, paquete.getProtocoloSobreIp());
            if (paquete.tieneAplicacion()) {
                incrementar(porAplicacion, paquete.getProtocoloAplicacion());
            }

            CabeceraSobreIP sobreIp = paquete.getSobreIp();
            // Solo TCP y UDP tienen puertos: ICMP queda fuera por diseno de la jerarquia
            if (sobreIp instanceof CabeceraConPuertos) {
                CabeceraConPuertos conPuertos = (CabeceraConPuertos) sobreIp;
                paquetesConPuertos++;
                incrementar(puertosDestino, String.valueOf(conPuertos.getPuertoDestino()));
                ServicioConocido servicio = conPuertos.getServicio();
                incrementar(porServicio, servicio != null ? servicio.getNombre() : SIN_PUERTO_CONOCIDO);
            } else if (sobreIp instanceof CabeceraICMP) {
                incrementar(porServicio, SIN_PUERTOS_ICMP);
            }

            if (sobreIp instanceof CabeceraTCP) {
                CabeceraTCP tcp = (CabeceraTCP) sobreIp;
                incrementar(flagsPorCombinacion, tcp.getFlags().toUpperCase());
                for (String flag : tcp.getListaFlags()) {
                    incrementar(flagsPorBit, flag);
                }
            }

            clasificarConsultaRespuesta(paquete, consultas, respuestas);
        }

        ResumenEstadistico resumen = new ResumenEstadistico();
        resumen.setLineas(resultado.getLineasLeidas(), resultado.getLineasVacias(),
                resultado.getErrores().size());
        resumen.setTotalPaquetes(paquetes.size());
        resumen.setPorProtocoloSobreIp(ordenarPorFrecuencia(porProtocoloSobreIp));
        resumen.setPorAplicacion(ordenarPorFrecuencia(porAplicacion));
        resumen.setPorServicio(ordenarPorFrecuencia(porServicio));
        resumen.setIpOrigen(masFrecuentes(ipsOrigen), frecuenciaMaxima(ipsOrigen));
        resumen.setIpDestino(masFrecuentes(ipsDestino), frecuenciaMaxima(ipsDestino));
        resumen.setPuertoDestino(masFrecuentes(puertosDestino), frecuenciaMaxima(puertosDestino),
                paquetesConPuertos);
        resumen.setFlags(ordenarPorFrecuencia(flagsPorCombinacion), ordenarPorFrecuencia(flagsPorBit));
        resumen.setConsultasYRespuestas(consultas, respuestas);
        return resumen;
    }

    /**
     * Identifica consultas y respuestas segun las reglas de cada protocolo.
     * ICMP se lee de su propia cabecera (tipo 8 = solicitud, tipo 0 = respuesta);
     * el resto, de los datos de aplicacion.
     */
    private void clasificarConsultaRespuesta(Paquete paquete, Map<String, Integer> consultas,
                                             Map<String, Integer> respuestas) {
        if (paquete.getSobreIp() instanceof CabeceraICMP) {
            CabeceraICMP icmp = (CabeceraICMP) paquete.getSobreIp();
            if (icmp.esSolicitud()) {
                incrementar(consultas, "ICMP");
            } else if (icmp.esRespuesta()) {
                incrementar(respuestas, "ICMP");
            }
            return;
        }
        if (!paquete.tieneAplicacion()) {
            return;
        }
        CabeceraAplicacion app = paquete.getAplicacion();
        String protocolo = app.getProtocolo().toUpperCase();
        boolean esConsulta;
        boolean esRespuesta;
        switch (protocolo) {
            case "DNS":
                esConsulta = app.datosEmpiezanCon("QUERY_");
                esRespuesta = app.datosEmpiezanCon("RESPONSE_");
                break;
            case "NTP":
                esConsulta = app.datosEmpiezanCon("TIME_REQUEST");
                esRespuesta = app.datosEmpiezanCon("TIME_RESPONSE");
                break;
            case "SNMP":
                esConsulta = app.datosEmpiezanCon("GET_", "GETNEXT_", "SET_");
                esRespuesta = app.datosEmpiezanCon("RESPONSE_");
                break;
            case "DHCP":
                esConsulta = app.datosEmpiezanCon("DISCOVER", "REQUEST");
                esRespuesta = app.datosEmpiezanCon("OFFER", "ACK");
                break;
            default:
                return; // otros protocolos no siguen un esquema simple de consulta/respuesta
        }
        if (esConsulta) {
            incrementar(consultas, protocolo);
        } else if (esRespuesta) {
            incrementar(respuestas, protocolo);
        }
    }

    private static void incrementar(Map<String, Integer> mapa, String clave) {
        mapa.merge(clave, 1, Integer::sum);
    }

    private static int frecuenciaMaxima(Map<String, Integer> mapa) {
        int maximo = 0;
        for (int valor : mapa.values()) {
            maximo = Math.max(maximo, valor);
        }
        return maximo;
    }

    /**
     * Claves con la frecuencia maxima. Si hay empate se devuelven todas, en el orden en que
     * aparecieron en el archivo, para no depender de un desempate arbitrario.
     */
    private static List<String> masFrecuentes(Map<String, Integer> mapa) {
        int maximo = frecuenciaMaxima(mapa);
        List<String> resultado = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : mapa.entrySet()) {
            if (entrada.getValue() == maximo) {
                resultado.add(entrada.getKey());
            }
        }
        return resultado;
    }

    /** Copia del mapa ordenada de mayor a menor frecuencia (empates en orden alfabetico). */
    private static Map<String, Integer> ordenarPorFrecuencia(Map<String, Integer> mapa) {
        List<Map.Entry<String, Integer>> entradas = new ArrayList<>(mapa.entrySet());
        entradas.sort((a, b) -> {
            int comparacion = Integer.compare(b.getValue(), a.getValue());
            return comparacion != 0 ? comparacion : a.getKey().compareTo(b.getKey());
        });
        Map<String, Integer> ordenado = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : entradas) {
            ordenado.put(entrada.getKey(), entrada.getValue());
        }
        return ordenado;
    }
}
