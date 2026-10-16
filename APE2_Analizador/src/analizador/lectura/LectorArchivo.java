package analizador.lectura;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lee el archivo de paquetes linea por linea. Solo lee: no interpreta el contenido.
 * La posicion de cada linea en la lista (indice + 1) es su numero de linea en el archivo.
 */
public class LectorArchivo {

    /** Marca de orden de bytes que algunos editores de Windows agregan al inicio en UTF-8. */
    private static final char BOM = '\uFEFF';

    /**
     * Lee todas las lineas del archivo en UTF-8. Tolera finales de linea de Windows (CRLF)
     * o Linux (LF) y elimina la marca BOM si existe.
     *
     * @throws IOException si el archivo no existe o no puede leerse
     */
    public List<String> leer(String ruta) throws IOException {
        Path archivo = Paths.get(ruta);
        List<String> lineas = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (lineas.isEmpty() && !linea.isEmpty() && linea.charAt(0) == BOM) {
                    linea = linea.substring(1);
                }
                lineas.add(linea);
            }
        }
        return Collections.unmodifiableList(lineas);
    }
}
