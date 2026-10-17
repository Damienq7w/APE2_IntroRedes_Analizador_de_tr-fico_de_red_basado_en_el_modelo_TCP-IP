package analizador.app;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Clase principal: solo arranca el programa. Toda la logica esta repartida en los
 * demas paquetes (lectura, analisis, estadistica, consulta y reporte).
 *
 * Uso: analizador.app.Main recursos/paquetes.txt, tanto si se ejecuta desde la carpeta
 * APE2_Analizador como desde la raiz del repositorio (lo habitual en VS Code).
 */
public class Main {

    private static final String[] RUTAS_POR_DEFECTO = {
        "recursos/paquetes.txt",
        "APE2_Analizador/recursos/paquetes.txt"
    };

    public static void main(String[] args) {
        String ruta = args.length > 0 ? args[0] : buscarArchivoPorDefecto();
        new MenuPrincipal(ruta).iniciar();
    }

    private static String buscarArchivoPorDefecto() {
        for (String ruta : RUTAS_POR_DEFECTO) {
            if (Files.exists(Paths.get(ruta))) {
                return ruta;
            }
        }
        return RUTAS_POR_DEFECTO[0];
    }
}
