package analizador.estadistica;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Datos del resumen estadistico final. Solo almacena resultados: los calcula
 * GeneradorEstadisticas, que es la unica clase que puede asignarlos (setters de paquete).
 * Todos los mapas se entregan ordenados de mayor a menor frecuencia y son de solo lectura.
 */
public class ResumenEstadistico {

    private int lineasLeidas;
    private int lineasVacias;
    private int lineasConError;
    private int totalPaquetes;

    private Map<String, Integer> porProtocoloSobreIp = Collections.emptyMap();
    private Map<String, Integer> porAplicacion = Collections.emptyMap();
    private Map<String, Integer> porServicio = Collections.emptyMap();

    private List<String> ipsOrigenMasFrecuentes = Collections.emptyList();
    private int frecuenciaIpOrigen;
    private List<String> ipsDestinoMasFrecuentes = Collections.emptyList();
    private int frecuenciaIpDestino;
    private List<String> puertosDestinoMasFrecuentes = Collections.emptyList();
    private int frecuenciaPuertoDestino;
    private int paquetesConPuertos;

    private Map<String, Integer> flagsPorCombinacion = Collections.emptyMap();
    private Map<String, Integer> flagsPorBit = Collections.emptyMap();

    private Map<String, Integer> consultasPorProtocolo = Collections.emptyMap();
    private Map<String, Integer> respuestasPorProtocolo = Collections.emptyMap();

    ResumenEstadistico() {
        // Solo GeneradorEstadisticas crea resumenes
    }

    // ---------------- Lineas y totales ----------------

    public int getLineasLeidas() {
        return lineasLeidas;
    }

    public int getLineasVacias() {
        return lineasVacias;
    }

    public int getLineasConError() {
        return lineasConError;
    }

    public int getTotalPaquetes() {
        return totalPaquetes;
    }

    // ---------------- Protocolos ----------------

    /** Conteo de TCP, UDP e ICMP (protocolos que viajan dentro de IP). */
    public Map<String, Integer> getPorProtocoloSobreIp() {
        return porProtocoloSobreIp;
    }

    public int getCantidadTcp() {
        return porProtocoloSobreIp.getOrDefault("TCP", 0);
    }

    public int getCantidadUdp() {
        return porProtocoloSobreIp.getOrDefault("UDP", 0);
    }

    public int getCantidadIcmp() {
        return porProtocoloSobreIp.getOrDefault("ICMP", 0);
    }

    public Map<String, Integer> getPorAplicacion() {
        return porAplicacion;
    }

    /** Paquetes por servicio, identificado por su puerto bien conocido. */
    public Map<String, Integer> getPorServicio() {
        return porServicio;
    }

    // ---------------- Mas frecuentes ----------------

    /** Lista con todas las IP empatadas en la maxima frecuencia. */
    public List<String> getIpsOrigenMasFrecuentes() {
        return ipsOrigenMasFrecuentes;
    }

    public int getFrecuenciaIpOrigen() {
        return frecuenciaIpOrigen;
    }

    public List<String> getIpsDestinoMasFrecuentes() {
        return ipsDestinoMasFrecuentes;
    }

    public int getFrecuenciaIpDestino() {
        return frecuenciaIpDestino;
    }

    public List<String> getPuertosDestinoMasFrecuentes() {
        return puertosDestinoMasFrecuentes;
    }

    public int getFrecuenciaPuertoDestino() {
        return frecuenciaPuertoDestino;
    }

    /** Paquetes con puertos (TCP + UDP), base del calculo del puerto destino. */
    public int getPaquetesConPuertos() {
        return paquetesConPuertos;
    }

    // ---------------- TCP y consultas ----------------

    /** Flags tal como aparecen en el archivo, por ejemplo "PSH-ACK". */
    public Map<String, Integer> getFlagsPorCombinacion() {
        return flagsPorCombinacion;
    }

    /** Cada flag contado por separado: un "PSH-ACK" suma un PSH y un ACK. */
    public Map<String, Integer> getFlagsPorBit() {
        return flagsPorBit;
    }

    public Map<String, Integer> getConsultasPorProtocolo() {
        return consultasPorProtocolo;
    }

    public Map<String, Integer> getRespuestasPorProtocolo() {
        return respuestasPorProtocolo;
    }

    public int getTotalConsultas() {
        return sumar(consultasPorProtocolo);
    }

    public int getTotalRespuestas() {
        return sumar(respuestasPorProtocolo);
    }

    private static int sumar(Map<String, Integer> mapa) {
        int total = 0;
        for (int valor : mapa.values()) {
            total += valor;
        }
        return total;
    }

    private static Map<String, Integer> soloLectura(Map<String, Integer> mapa) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(mapa));
    }

    // ---------------- Asignacion (solo desde el paquete estadistica) ----------------

    void setLineas(int leidas, int vacias, int conError) {
        this.lineasLeidas = leidas;
        this.lineasVacias = vacias;
        this.lineasConError = conError;
    }

    void setTotalPaquetes(int totalPaquetes) {
        this.totalPaquetes = totalPaquetes;
    }

    void setPorProtocoloSobreIp(Map<String, Integer> mapa) {
        this.porProtocoloSobreIp = soloLectura(mapa);
    }

    void setPorAplicacion(Map<String, Integer> mapa) {
        this.porAplicacion = soloLectura(mapa);
    }

    void setPorServicio(Map<String, Integer> mapa) {
        this.porServicio = soloLectura(mapa);
    }

    void setIpOrigen(List<String> ips, int frecuencia) {
        this.ipsOrigenMasFrecuentes = Collections.unmodifiableList(ips);
        this.frecuenciaIpOrigen = frecuencia;
    }

    void setIpDestino(List<String> ips, int frecuencia) {
        this.ipsDestinoMasFrecuentes = Collections.unmodifiableList(ips);
        this.frecuenciaIpDestino = frecuencia;
    }

    void setPuertoDestino(List<String> puertos, int frecuencia, int paquetesConPuertos) {
        this.puertosDestinoMasFrecuentes = Collections.unmodifiableList(puertos);
        this.frecuenciaPuertoDestino = frecuencia;
        this.paquetesConPuertos = paquetesConPuertos;
    }

    void setFlags(Map<String, Integer> porCombinacion, Map<String, Integer> porBit) {
        this.flagsPorCombinacion = soloLectura(porCombinacion);
        this.flagsPorBit = soloLectura(porBit);
    }

    void setConsultasYRespuestas(Map<String, Integer> consultas, Map<String, Integer> respuestas) {
        this.consultasPorProtocolo = soloLectura(consultas);
        this.respuestasPorProtocolo = soloLectura(respuestas);
    }
}
