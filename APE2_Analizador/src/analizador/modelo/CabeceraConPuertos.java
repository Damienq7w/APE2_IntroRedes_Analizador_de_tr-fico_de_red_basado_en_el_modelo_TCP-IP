package analizador.modelo;

import analizador.enums.Capa;
import analizador.enums.ServicioConocido;
import analizador.excepciones.PaqueteMalFormadoException;

import java.util.Map;

/**
 * Cabecera de transporte con puertos (TCP y UDP). El puerto identifica el proceso dentro
 * del host; ICMP no tiene puertos, por eso no hereda de esta clase.
 */
public abstract class CabeceraConPuertos extends CabeceraSobreIP {

    protected CabeceraConPuertos(String nombre, Map<String, String> campos) {
        super(nombre, campos);
    }

    @Override
    public final Capa getCapa() {
        return Capa.TRANSPORTE;
    }

    @Override
    protected final void validarFormato() throws PaqueteMalFormadoException {
        // Los puertos son campos de 16 bits
        exigirEnteroEnRango("sport", 0, 65535);
        exigirEnteroEnRango("dport", 0, 65535);
        validarCamposPropios();
    }

    /** Validaciones de los campos que agrega cada protocolo (TCP o UDP). */
    protected abstract void validarCamposPropios() throws PaqueteMalFormadoException;

    public int getPuertoOrigen() {
        return leerEntero("sport");
    }

    public int getPuertoDestino() {
        return leerEntero("dport");
    }

    /**
     * Servicio bien conocido de la comunicacion: se busca primero por el puerto destino
     * (solicitudes) y despues por el puerto origen (respuestas del servidor).
     *
     * @return el servicio, o null si ninguno de los dos puertos es conocido
     */
    public ServicioConocido getServicio() {
        ServicioConocido servicio = ServicioConocido.porPuerto(getPuertoDestino());
        if (servicio == null) {
            servicio = ServicioConocido.porPuerto(getPuertoOrigen());
        }
        return servicio;
    }
}
