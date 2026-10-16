package analizador.reporte;

import analizador.analisis.ResultadoAnalisis;
import analizador.enums.ServicioConocido;
import analizador.estadistica.ResumenEstadistico;
import analizador.excepciones.PaqueteMalFormadoException;
import analizador.modelo.CabeceraAplicacion;
import analizador.modelo.CabeceraEthernet;
import analizador.modelo.CabeceraICMP;
import analizador.modelo.CabeceraIP;
import analizador.modelo.CabeceraSobreIP;
import analizador.modelo.CabeceraTCP;
import analizador.modelo.CabeceraUDP;
import analizador.modelo.Paquete;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Construye todos los textos del reporte y los devuelve como String.
 * No imprime ni escribe archivos: asi la consola y el archivo exportado muestran
 * exactamente lo mismo, y el formato se cambia en un solo lugar.
 */
public class FormateadorReporte {

    private static final String SALTO = "\n";
    private static final int ANCHO_ETIQUETA = 19;
    private static final int ANCHO_ETIQUETA_RESUMEN = 30;
    private static final int MAXIMO_CONTENIDO_ERROR = 110;

    private final ResultadoAnalisis resultado;

    public FormateadorReporte(ResultadoAnalisis resultado) {
        this.resultado = resultado;
    }

    // =====================================================================
    // Paquetes
    // =====================================================================

    /** Desglose de un paquete por capas del modelo TCP/IP. */
    public String formatearPaquete(Paquete paquete) {
        StringBuilder texto = new StringBuilder();
        texto.append(titulo("PAQUETE #" + resultado.getNumeroDe(paquete)
                + " (linea " + paquete.getNumeroLinea() + ")"));
        agregarEthernet(texto, paquete.getEthernet());
        agregarIp(texto, paquete.getIp());
        agregarSobreIp(texto, paquete.getSobreIp());
        agregarAplicacion(texto, paquete.getAplicacion());
        return texto.toString();
    }

    /** Varios paquetes seguidos, separados por una linea en blanco. */
    public String formatearPaquetes(List<Paquete> paquetes) {
        if (paquetes.isEmpty()) {
            return "No se encontraron paquetes." + SALTO;
        }
        StringBuilder texto = new StringBuilder();
        for (Paquete paquete : paquetes) {
            texto.append(formatearPaquete(paquete)).append(SALTO);
        }
        return texto.toString();
    }

    private void agregarEthernet(StringBuilder texto, CabeceraEthernet eth) {
        texto.append(encabezadoCapa(eth.getCapa().getEtiqueta(), "Ethernet"));
        texto.append(campo("MAC origen", eth.getMacOrigen()));
        texto.append(campo("MAC destino", eth.getMacDestino()));
        texto.append(campo("Tipo de trama", eth.getTipoTrama()));
    }

    private void agregarIp(StringBuilder texto, CabeceraIP ip) {
        texto.append(encabezadoCapa(ip.getCapa().getEtiqueta(), "IP"));
        texto.append(campo("IP origen", ip.getIpOrigen()));
        texto.append(campo("IP destino", ip.getIpDestino()));
        texto.append(campo("TTL", String.valueOf(ip.getTtl())));
        texto.append(campo("Protocolo", ip.getProtocolo()));
    }

    private void agregarSobreIp(StringBuilder texto, CabeceraSobreIP sobreIp) {
        if (sobreIp instanceof CabeceraICMP) {
            CabeceraICMP icmp = (CabeceraICMP) sobreIp;
            texto.append(encabezadoCapa(icmp.getCapa().getEtiqueta(),
                    "ICMP (mensaje de control encapsulado en IP; ocupa la posicion de transporte)"));
            texto.append(campo("Tipo de protocolo", icmp.getTipoProtocolo()));
            texto.append(campo("Tipo ICMP", icmp.getTipo() + " (" + icmp.getDescripcionTipo() + ")"));
            texto.append(campo("Codigo", String.valueOf(icmp.getCodigo())));
            texto.append(campo("Identificador", String.valueOf(icmp.getIdentificador())));
            texto.append(campo("Secuencia", String.valueOf(icmp.getSecuencia())));
            texto.append(campo("Puertos", "no aplica (ICMP no usa puertos)"));
            return;
        }
        if (sobreIp instanceof CabeceraTCP) {
            CabeceraTCP tcp = (CabeceraTCP) sobreIp;
            texto.append(encabezadoCapa(tcp.getCapa().getEtiqueta(), "TCP"));
            texto.append(campo("Tipo de protocolo", tcp.getTipoProtocolo()));
            texto.append(campo("Puerto origen", String.valueOf(tcp.getPuertoOrigen())));
            texto.append(campo("Puerto destino", conServicio(tcp.getPuertoDestino())));
            texto.append(campo("Secuencia", String.valueOf(tcp.getSecuencia())));
            texto.append(campo("ACK", String.valueOf(tcp.getAck())));
            texto.append(campo("Flags", tcp.getFlags()));
            return;
        }
        if (sobreIp instanceof CabeceraUDP) {
            CabeceraUDP udp = (CabeceraUDP) sobreIp;
            texto.append(encabezadoCapa(udp.getCapa().getEtiqueta(), "UDP"));
            texto.append(campo("Tipo de protocolo", udp.getTipoProtocolo()));
            texto.append(campo("Puerto origen", conServicio(udp.getPuertoOrigen())));
            texto.append(campo("Puerto destino", conServicio(udp.getPuertoDestino())));
            texto.append(campo("Longitud", udp.getLongitud() + " bytes"));
        }
    }

    private void agregarAplicacion(StringBuilder texto, CabeceraAplicacion app) {
        if (app == null) {
            texto.append("[CAPA DE APLICACION] (el paquete no trae datos de aplicacion)").append(SALTO);
            return;
        }
        texto.append(encabezadoCapa(app.getCapa().getEtiqueta(), app.getProtocolo()));
        texto.append(campo("Protocolo", app.getProtocolo()));
        texto.append(campo("Datos", app.getDatos()));
    }

    /** Agrega el nombre del servicio cuando el puerto es bien conocido, por ejemplo "80 (HTTP)". */
    private String conServicio(int puerto) {
        ServicioConocido servicio = ServicioConocido.porPuerto(puerto);
        return servicio == null ? String.valueOf(puerto) : puerto + " (" + servicio.getNombre() + ")";
    }

    // =====================================================================
    // Resumen estadistico
    // =====================================================================

    public String formatearResumen(ResumenEstadistico resumen) {
        StringBuilder texto = new StringBuilder();
        texto.append(titulo("RESUMEN ESTADISTICO"));
        texto.append(filaResumen("Lineas leidas del archivo", resumen.getLineasLeidas()));
        texto.append(filaResumen("Lineas vacias (separadores)", resumen.getLineasVacias()));
        texto.append(filaResumen("Lineas con error", resumen.getLineasConError()));
        texto.append(filaResumen("Total de paquetes procesados", resumen.getTotalPaquetes()));

        int total = resumen.getTotalPaquetes();
        texto.append(SALTO).append("-- Protocolo sobre IP --").append(SALTO);
        texto.append(filaConPorcentaje("Paquetes TCP", resumen.getCantidadTcp(), total));
        texto.append(filaConPorcentaje("Paquetes UDP", resumen.getCantidadUdp(), total));
        texto.append(filaConPorcentaje("Paquetes ICMP", resumen.getCantidadIcmp(), total));

        texto.append(SALTO).append("-- Paquetes por protocolo de aplicacion --").append(SALTO);
        texto.append(tablaConteo(resumen.getPorAplicacion(), total));

        texto.append(SALTO).append("-- Mas frecuentes --").append(SALTO);
        texto.append(fila("IP origen mas frecuente",
                unir(resumen.getIpsOrigenMasFrecuentes()) + " (" + resumen.getFrecuenciaIpOrigen() + " paquetes"
                        + empate(resumen.getIpsOrigenMasFrecuentes()) + ")"));
        texto.append(fila("IP destino mas frecuente",
                unir(resumen.getIpsDestinoMasFrecuentes()) + " (" + resumen.getFrecuenciaIpDestino() + " paquetes"
                        + empate(resumen.getIpsDestinoMasFrecuentes()) + ")"));
        texto.append(fila("Puerto destino mas frecuente",
                puertosConServicio(resumen.getPuertosDestinoMasFrecuentes()) + " ("
                        + resumen.getFrecuenciaPuertoDestino() + " de " + resumen.getPaquetesConPuertos()
                        + " paquetes TCP/UDP" + empate(resumen.getPuertosDestinoMasFrecuentes()) + ")"));

        texto.append(SALTO).append("-- Flags TCP --").append(SALTO);
        texto.append("   Por combinacion:").append(SALTO);
        texto.append(tablaConteo(resumen.getFlagsPorCombinacion(), -1));
        texto.append("   Por flag individual:").append(SALTO);
        texto.append(tablaConteo(resumen.getFlagsPorBit(), -1));

        texto.append(SALTO).append("-- Consultas y respuestas --").append(SALTO);
        texto.append(String.format("   %-10s %10s %11s\n", "Protocolo", "Consultas", "Respuestas"));
        for (String protocolo : protocolosConsultaRespuesta(resumen)) {
            texto.append(String.format("   %-10s %10d %11d\n", protocolo,
                    resumen.getConsultasPorProtocolo().getOrDefault(protocolo, 0),
                    resumen.getRespuestasPorProtocolo().getOrDefault(protocolo, 0)));
        }
        texto.append(String.format("   %-10s %10d %11d\n", "TOTAL",
                resumen.getTotalConsultas(), resumen.getTotalRespuestas()));

        texto.append(SALTO).append("-- Resumen por servicio (segun puerto bien conocido) --").append(SALTO);
        texto.append(tablaConteo(resumen.getPorServicio(), total));
        return texto.toString();
    }

    private List<String> protocolosConsultaRespuesta(ResumenEstadistico resumen) {
        List<String> protocolos = new ArrayList<>(resumen.getConsultasPorProtocolo().keySet());
        for (String protocolo : resumen.getRespuestasPorProtocolo().keySet()) {
            if (!protocolos.contains(protocolo)) {
                protocolos.add(protocolo);
            }
        }
        return protocolos;
    }

    private String puertosConServicio(List<String> puertos) {
        StringBuilder texto = new StringBuilder();
        for (String puerto : puertos) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(conServicio(Integer.parseInt(puerto)));
        }
        return texto.toString();
    }

    private String empate(List<String> valores) {
        return valores.size() > 1 ? " cada una, empate" : "";
    }

    private String unir(List<String> valores) {
        return valores.isEmpty() ? "-" : String.join(", ", valores);
    }

    /** Tabla clave/cantidad; si total > 0 agrega el porcentaje. */
    private String tablaConteo(Map<String, Integer> conteo, int total) {
        if (conteo.isEmpty()) {
            return "   (sin datos)" + SALTO;
        }
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<String, Integer> entrada : conteo.entrySet()) {
            if (total > 0) {
                texto.append(String.format("   %-22s %5d   %s\n", entrada.getKey(), entrada.getValue(),
                        porcentaje(entrada.getValue(), total)));
            } else {
                texto.append(String.format("   %-22s %5d\n", entrada.getKey(), entrada.getValue()));
            }
        }
        return texto.toString();
    }

    private String filaConPorcentaje(String etiqueta, int valor, int total) {
        return fila(etiqueta, valor + "   " + porcentaje(valor, total));
    }

    private String porcentaje(int valor, int total) {
        double porcentaje = total == 0 ? 0 : valor * 100.0 / total;
        return String.format(Locale.ROOT, "(%.1f %%)", porcentaje).replace('.', ',');
    }

    private String filaResumen(String etiqueta, int valor) {
        return fila(etiqueta, String.valueOf(valor));
    }

    private String fila(String etiqueta, String valor) {
        return relleno(etiqueta, ANCHO_ETIQUETA_RESUMEN, "") + valor + SALTO;
    }

    // =====================================================================
    // Errores y carga
    // =====================================================================

    public String formatearErrores() {
        List<PaqueteMalFormadoException> errores = resultado.getErrores();
        StringBuilder texto = new StringBuilder();
        texto.append(titulo("LINEAS CON ERROR (" + errores.size() + ")"));
        if (errores.isEmpty()) {
            texto.append("No se encontraron lineas mal formadas: todas las lineas no vacias")
                    .append(" se interpretaron correctamente.").append(SALTO);
            return texto.toString();
        }
        for (PaqueteMalFormadoException error : errores) {
            texto.append("Linea ").append(error.getNumeroLinea())
                    .append(" [").append(error.getTipo().getDescripcion()).append("]: ")
                    .append(error.getMotivo()).append(SALTO);
            texto.append("   Contenido: ").append(abreviar(error.getContenido())).append(SALTO);
        }
        return texto.toString();
    }

    /** Resumen breve que se muestra al cargar el archivo. */
    public String formatearCarga(String rutaArchivo) {
        return "Archivo analizado ....: " + rutaArchivo + SALTO
                + "Lineas leidas ........: " + resultado.getLineasLeidas() + SALTO
                + "Paquetes validos .....: " + resultado.getValidos().size() + SALTO
                + "Lineas vacias ........: " + resultado.getLineasVacias() + SALTO
                + "Lineas con error .....: " + resultado.getErrores().size() + SALTO;
    }

    /** Reporte completo: carga, todos los paquetes, resumen y errores. */
    public String formatearReporteCompleto(String rutaArchivo, ResumenEstadistico resumen) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return titulo("ANALIZADOR DE TRAFICO TCP/IP - REPORTE")
                + "Generado .............: " + fecha + SALTO
                + formatearCarga(rutaArchivo) + SALTO
                + formatearPaquetes(resultado.getValidos()) + SALTO
                + formatearResumen(resumen) + SALTO
                + formatearErrores();
    }

    // =====================================================================
    // Utilidades de formato
    // =====================================================================

    private String titulo(String texto) {
        return "================ " + texto + " ================" + SALTO;
    }

    private String encabezadoCapa(String etiquetaCapa, String protocolo) {
        return "[" + etiquetaCapa + "] " + protocolo + SALTO;
    }

    /** Linea "   Etiqueta ......: valor" con los dos puntos alineados. */
    private String campo(String etiqueta, String valor) {
        return relleno(etiqueta, ANCHO_ETIQUETA, "   ") + valor + SALTO;
    }

    private String relleno(String etiqueta, int ancho, String sangria) {
        int puntos = Math.max(2, ancho - etiqueta.length());
        return sangria + etiqueta + " " + ".".repeat(puntos) + ": ";
    }

    private String abreviar(String texto) {
        return texto.length() <= MAXIMO_CONTENIDO_ERROR ? texto
                : texto.substring(0, MAXIMO_CONTENIDO_ERROR) + "...";
    }
}
