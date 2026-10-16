package analizador.app;

import analizador.analisis.AnalizadorTrafico;
import analizador.analisis.ResultadoAnalisis;
import analizador.analisis.ValidadorCampos;
import analizador.consulta.FiltroPaquetes;
import analizador.estadistica.GeneradorEstadisticas;
import analizador.estadistica.ResumenEstadistico;
import analizador.lectura.LectorArchivo;
import analizador.modelo.Paquete;
import analizador.reporte.ExportadorReporte;
import analizador.reporte.FormateadorReporte;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Menu interactivo por consola. Coordina las demas clases: carga y analiza el archivo
 * una sola vez y luego atiende las opciones del usuario. Toda entrada se valida para
 * que un dato incorrecto (por ejemplo una letra) no cierre el programa.
 */
public class MenuPrincipal {

    private static final int OPCION_SALIR = 0;
    private static final int OPCION_INVALIDA = -1;
    private static final String ARCHIVO_REPORTE_POR_DEFECTO = "reporte_analisis.txt";

    private final String rutaArchivo;
    private final Scanner entrada;
    private final FiltroPaquetes filtro;

    private ResultadoAnalisis resultado;
    private ResumenEstadistico resumen;
    private FormateadorReporte formateador;
    private boolean finDeEntrada;

    public MenuPrincipal(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.entrada = new Scanner(System.in);
        this.filtro = new FiltroPaquetes();
    }

    /** Punto de entrada del menu: analiza el archivo y atiende opciones hasta que el usuario sale. */
    public void iniciar() {
        System.out.println();
        System.out.println("=====================================================");
        System.out.println("   ANALIZADOR DE TRAFICO DE RED - MODELO TCP/IP");
        System.out.println("=====================================================");
        if (!cargarDatos()) {
            return;
        }
        int opcion;
        do {
            mostrarOpciones();
            opcion = leerEntero("Seleccione una opcion: ");
            System.out.println();
            ejecutar(opcion);
        } while (opcion != OPCION_SALIR);
    }

    private boolean cargarDatos() {
        List<String> lineas;
        try {
            lineas = new LectorArchivo().leer(rutaArchivo);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo '" + rutaArchivo + "': " + e.getMessage());
            System.out.println("Indique la ruta del archivo como argumento del programa (ver .vscode/launch.json).");
            return false;
        }
        resultado = new AnalizadorTrafico().procesar(lineas);
        resumen = new GeneradorEstadisticas().generar(resultado);
        formateador = new FormateadorReporte(resultado);

        System.out.print(formateador.formatearCarga(rutaArchivo));
        if (!resultado.getErrores().isEmpty()) {
            System.out.println("Atencion: hay lineas con error. Revise la opcion 7 del menu.");
        }
        return true;
    }

    private void mostrarOpciones() {
        System.out.println();
        System.out.println("------------------- MENU PRINCIPAL -------------------");
        System.out.println(" 1. Mostrar todos los paquetes por capas");
        System.out.println(" 2. Ver un paquete por numero");
        System.out.println(" 3. Resumen estadistico");
        System.out.println(" 4. Buscar paquetes por IP (origen o destino)");
        System.out.println(" 5. Filtrar por protocolo de transporte (TCP/UDP/ICMP)");
        System.out.println(" 6. Filtrar por protocolo de aplicacion");
        System.out.println(" 7. Ver lineas con error");
        System.out.println(" 8. Exportar reporte a archivo");
        System.out.println(" 0. Salir");
        System.out.println("------------------------------------------------------");
    }

    private void ejecutar(int opcion) {
        switch (opcion) {
            case 1:
                System.out.print(formateador.formatearPaquetes(resultado.getValidos()));
                break;
            case 2:
                verPaquetePorNumero();
                break;
            case 3:
                System.out.print(formateador.formatearResumen(resumen));
                break;
            case 4:
                buscarPorIp();
                break;
            case 5:
                filtrarPorTransporte();
                break;
            case 6:
                filtrarPorAplicacion();
                break;
            case 7:
                System.out.print(formateador.formatearErrores());
                break;
            case 8:
                exportarReporte();
                break;
            case OPCION_SALIR:
                System.out.println("Fin del programa.");
                break;
            case OPCION_INVALIDA:
                break; // el mensaje ya se mostro al leer la opcion
            default:
                System.out.println("Opcion fuera de rango: elija un numero entre 0 y 8.");
        }
    }

    private void verPaquetePorNumero() {
        int total = resultado.getValidos().size();
        int numero = leerEntero("Numero de paquete (1-" + total + "): ");
        Paquete paquete = resultado.getPaquete(numero);
        if (paquete == null) {
            if (numero != OPCION_INVALIDA) {
                System.out.println("No existe el paquete " + numero + ". Hay " + total + " paquetes validos.");
            }
            return;
        }
        System.out.print(formateador.formatearPaquete(paquete));
    }

    private void buscarPorIp() {
        String ip = leerTexto("IP a buscar: ");
        if (!ValidadorCampos.esIpv4Valida(ip)) {
            System.out.println("'" + ip + "' no es una direccion IPv4 valida (ejemplo: 192.168.1.10).");
            return;
        }
        mostrarResultadoFiltro("con IP " + ip + " como origen o destino", filtro.porIp(resultado.getValidos(), ip));
    }

    private void filtrarPorTransporte() {
        String protocolo = leerTexto("Protocolo (TCP, UDP o ICMP): ").toUpperCase();
        if (!protocolo.equals("TCP") && !protocolo.equals("UDP") && !protocolo.equals("ICMP")) {
            System.out.println("Protocolo no reconocido: escriba TCP, UDP o ICMP.");
            return;
        }
        mostrarResultadoFiltro(protocolo, filtro.porTransporte(resultado.getValidos(), protocolo));
    }

    private void filtrarPorAplicacion() {
        System.out.println("Protocolos presentes: " + String.join(", ", resumen.getPorAplicacion().keySet()));
        String protocolo = leerTexto("Protocolo de aplicacion: ").toUpperCase();
        if (protocolo.isEmpty()) {
            System.out.println("No se ingreso ningun protocolo.");
            return;
        }
        mostrarResultadoFiltro(protocolo, filtro.porAplicacion(resultado.getValidos(), protocolo));
    }

    private void mostrarResultadoFiltro(String descripcion, List<Paquete> encontrados) {
        System.out.println("Se encontraron " + encontrados.size() + " paquetes " + descripcion + ".");
        System.out.println();
        System.out.print(formateador.formatearPaquetes(encontrados));
    }

    private void exportarReporte() {
        String nombre = leerTexto("Nombre del archivo [" + ARCHIVO_REPORTE_POR_DEFECTO + "]: ");
        if (nombre.isEmpty()) {
            nombre = ARCHIVO_REPORTE_POR_DEFECTO;
        }
        if (nombre.contains("/") || nombre.contains("\\")) {
            System.out.println("Escriba solo el nombre del archivo; se guardara en la carpeta reportes.");
            return;
        }
        try {
            String contenido = formateador.formatearReporteCompleto(rutaArchivo, resumen);
            ExportadorReporte exportador = new ExportadorReporte();
            Path carpeta = exportador.carpetaReportesPara(rutaArchivo);
            Path destino = exportador.exportar(contenido, carpeta, nombre);
            System.out.println("Reporte exportado en: " + destino);
        } catch (IOException e) {
            System.out.println("No se pudo exportar el reporte: " + e.getMessage());
        }
    }

    // ---------------- Lectura segura de la consola ----------------

    /** Lee un entero; si el texto no es numerico avisa y devuelve OPCION_INVALIDA. */
    private int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        if (finDeEntrada) {
            return OPCION_SALIR; // ya no hay entrada estandar: se termina sin error
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida: '" + texto + "' no es un numero.");
            return OPCION_INVALIDA;
        }
    }

    /** Lee una linea de texto; devuelve "" si ya no hay entrada disponible. */
    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) {
            finDeEntrada = true;
            return "";
        }
        return entrada.nextLine().trim();
    }
}
