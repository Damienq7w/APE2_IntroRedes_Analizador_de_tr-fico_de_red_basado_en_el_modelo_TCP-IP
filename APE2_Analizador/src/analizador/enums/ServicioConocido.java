package analizador.enums;

/**
 * Puertos bien conocidos (asignados por IANA) presentes en el dataset y el servicio que identifican.
 * El puerto permite a la capa de transporte entregar los datos al proceso correcto.
 */
public enum ServicioConocido {

    FTP(21, "FTP", "TCP"),
    SSH(22, "SSH", "TCP"),
    SMTP(25, "SMTP", "TCP"),
    DNS(53, "DNS", "UDP"),
    DHCP_SERVIDOR(67, "DHCP", "UDP"),
    DHCP_CLIENTE(68, "DHCP", "UDP"),
    HTTP(80, "HTTP", "TCP"),
    POP3(110, "POP3", "TCP"),
    NTP(123, "NTP", "UDP"),
    IMAP(143, "IMAP", "TCP"),
    SNMP(161, "SNMP", "UDP"),
    HTTPS(443, "HTTPS", "TCP");

    private final int puerto;
    private final String nombre;
    private final String transporte;

    ServicioConocido(int puerto, String nombre, String transporte) {
        this.puerto = puerto;
        this.nombre = nombre;
        this.transporte = transporte;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getNombre() {
        return nombre;
    }

    /** Protocolo de transporte habitual del servicio (TCP o UDP). */
    public String getTransporte() {
        return transporte;
    }

    /**
     * Busca el servicio asociado a un puerto.
     *
     * @return el servicio, o null si el puerto no es uno de los conocidos
     */
    public static ServicioConocido porPuerto(int puerto) {
        for (ServicioConocido servicio : values()) {
            if (servicio.puerto == puerto) {
                return servicio;
            }
        }
        return null;
    }
}
