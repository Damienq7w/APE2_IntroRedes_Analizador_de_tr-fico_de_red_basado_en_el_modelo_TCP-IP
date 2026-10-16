package analizador.reporte;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Escribe en un archivo de texto el reporte que construye FormateadorReporte.
 * Los reportes se guardan en una carpeta "reportes" junto a la carpeta "recursos"
 * del archivo analizado; la carpeta se crea si todavia no existe.
 */
public class ExportadorReporte {

    private static final String CARPETA_RECURSOS = "recursos";
    private static final String CARPETA_REPORTES = "reportes";

    /**
     * Carpeta de reportes que corresponde al archivo analizado.
     * Ejemplo: APE2_Analizador/recursos/paquetes.txt -> APE2_Analizador/reportes
     */
    public Path carpetaReportesPara(String rutaArchivoAnalizado) {
        Path carpetaArchivo = Paths.get(rutaArchivoAnalizado).toAbsolutePath().getParent();
        Path base = carpetaArchivo;
        if (carpetaArchivo != null && carpetaArchivo.getFileName() != null
                && carpetaArchivo.getFileName().toString().equalsIgnoreCase(CARPETA_RECURSOS)) {
            base = carpetaArchivo.getParent();
        }
        if (base == null) {
            base = Paths.get("").toAbsolutePath();
        }
        return base.resolve(CARPETA_REPORTES);
    }

    /**
     * Guarda el contenido en UTF-8, con los saltos de linea del sistema operativo.
     *
     * @param carpeta       carpeta de destino (se crea si no existe)
     * @param nombreArchivo nombre del archivo, por ejemplo reporte_analisis.txt
     * @return la ruta absoluta del archivo generado
     * @throws IOException si la carpeta o el archivo no pueden escribirse
     */
    public Path exportar(String contenido, Path carpeta, String nombreArchivo) throws IOException {
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve(nombreArchivo).toAbsolutePath();
        String texto = contenido.replace("\n", System.lineSeparator());
        Files.write(destino, texto.getBytes(StandardCharsets.UTF_8));
        return destino;
    }
}
