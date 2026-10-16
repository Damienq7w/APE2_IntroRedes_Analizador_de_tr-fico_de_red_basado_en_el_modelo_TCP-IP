# APE2_IntroRedes_Analizador_de_tr-fico_de_red_basado_en_el_modelo_TCP-IP

Analizador de tráfico de red simulado, escrito en Java con programación orientada a objetos.
Lee `recursos/paquetes.txt`, separa cada paquete en las capas del modelo TCP/IP, muestra sus
campos y genera un resumen estadístico.

Práctica APE2 de **Introducción a Redes** (UTA, 3B, Ing. Daniel Maldonado-Ruiz, Ph.D.).

## Cómo ejecutarlo (VS Code)

Requisitos: JDK 11 o superior y la extensión **Extension Pack for Java** de VS Code.

1. Abrir en VS Code la carpeta raíz del repositorio (la que contiene `.vscode/` y `APE2_Analizador/`).
2. Ir a **Ejecutar y depurar** (Ctrl+Shift+D) y elegir una configuración:
   - **Analizar paquetes.txt**: analiza el dataset de la práctica.
   - **Probar lineas con error**: analiza `paquetes_prueba.txt`, que contiene líneas mal formadas a propósito.
3. Pulsar ▶ (F5). El menú se abre en la terminal integrada, donde se escriben las opciones.

También funciona el botón **Run** que aparece sobre el `main` de `Principal.java`: sin argumentos,
el programa busca solo `APE2_Analizador/recursos/paquetes.txt`.

La configuración está en `.vscode/settings.json` (carpeta de fuentes `APE2_Analizador/src`)
y `.vscode/launch.json` (clase principal, carpeta de trabajo y archivo a analizar).

## Reportes

La opción 8 del menú exporta el reporte completo (todos los paquetes, el resumen y los errores)
en **`APE2_Analizador/reportes/`**. La carpeta se crea sola la primera vez; el nombre por defecto es
`reporte_analisis.txt`.

## Menú

```
 1. Mostrar todos los paquetes por capas
 2. Ver un paquete por numero
 3. Resumen estadistico
 4. Buscar paquetes por IP (origen o destino)
 5. Filtrar por protocolo de transporte (TCP/UDP/ICMP)
 6. Filtrar por protocolo de aplicacion
 7. Ver lineas con error
 8. Exportar reporte a archivo
 0. Salir
```

## Estructura (26 clases, 9 paquetes)

```
.vscode/                  configuracion para ejecutar desde VS Code
APE2_Analizador/
├── recursos/             paquetes.txt (dataset) y paquetes_prueba.txt (lineas con error)
├── reportes/             reportes exportados (se crea al exportar)
└── src/analizador/       codigo fuente
```

```
APE2_Analizador/src/analizador/
├── app/           Principal, MenuPrincipal
├── enums/         Capa, TipoError, ServicioConocido
├── excepciones/   PaqueteMalFormadoException
├── modelo/        Cabecera, CabeceraEthernet, CabeceraIP, CabeceraSobreIP,
│                  CabeceraConPuertos, CabeceraTCP, CabeceraUDP, CabeceraICMP,
│                  CabeceraAplicacion, Paquete
├── lectura/       LectorArchivo
├── analisis/      ParserPaquete, ValidadorCampos, ResultadoAnalisis, AnalizadorTrafico
├── estadistica/   ResumenEstadistico, GeneradorEstadisticas
├── consulta/      FiltroPaquetes
└── reporte/       FormateadorReporte, ExportadorReporte
```

| Paquete | Responsabilidad |
|---|---|
| `app` | Arrancar el programa y atender el menú por consola |
| `enums` | Valores fijos: capas TCP/IP, tipo de error y puertos bien conocidos |
| `excepciones` | Error de una línea mal formada (línea, motivo y tipo) |
| `modelo` | El paquete y sus cabeceras (herencia + composición) |
| `lectura` | Leer el archivo línea por línea |
| `analisis` | Interpretar cada línea y validarla (sintaxis y semántica) |
| `estadistica` | Calcular el resumen final |
| `consulta` | Búsquedas y filtros |
| `reporte` | Construir los textos y exportarlos a archivo |

### Jerarquía de cabeceras

```
                     Cabecera (abstracta)
                           │
      ┌──────────────┬─────┴────────────┬────────────────────┐
CabeceraEthernet  CabeceraIP   CabeceraSobreIP (abstracta)  CabeceraAplicacion
                                       │
                         ┌─────────────┴─────────────┐
               CabeceraConPuertos (abstracta)    CabeceraICMP
                         │
                 ┌───────┴───────┐
            CabeceraTCP     CabeceraUDP
```

- `CabeceraSobreIP` agrupa lo que IP transporta en su carga útil (lo indica su campo `proto`).
- Solo TCP y UDP heredan de `CabeceraConPuertos`: ICMP no tiene puertos.
- ICMP ocupa la posición de transporte en el archivo, pero `getCapa()` devuelve **INTERNET**,
  porque es un protocolo de control de la capa de Internet.
- `Cabecera.validar()` aplica el patrón plantilla: primero comprueba los campos obligatorios
  (lógica común) y luego el formato propio de cada protocolo.

## Formato de entrada

```
ETH{src=..,dst=..,type=..}|IP{src=..,dst=..,ttl=..,proto=..}|TCP/UDP/ICMP{..}|APP{proto=..,data=..}
```

- El bloque `APP` es opcional (la guía indica que cada paquete trae sus capas "según corresponda").
- Las líneas vacías son separadores: se cuentan aparte, no son errores.
- Se toleran espacios, mayúsculas/minúsculas, finales de línea CRLF y la marca BOM de UTF-8.
- El valor de un campo puede contener `=` (por ejemplo `data=GET_/search?q=redes`).
- Limitación conocida: un valor no puede contener `,` ni `|`, porque son los separadores del formato.

## Validaciones

Una línea con error se registra y el análisis continúa con la siguiente.

| Tipo | Ejemplos |
|---|---|
| Sintáctico | Falta `}`, cabecera desconocida, número de bloques incorrecto, orden de bloques incorrecto, bloque o campo repetido, campo obligatorio ausente, MAC o IPv4 inválida, TTL fuera de 0–255, puerto fuera de 0–65535, `seq`/`ack` fuera de 32 bits, flag TCP desconocido, longitud UDP menor que 8 |
| Semántico | `IP proto=TCP` seguido de un bloque `UDP`; `ETH type` distinto de IPv4 cuando la trama transporta IP |

`recursos/paquetes_prueba.txt` contiene 21 líneas: 5 válidas (incluye BOM, espacios, minúsculas,
ICMP sin APP y `=` dentro de un dato), 1 vacía y 15 con un error distinto cada una.

## Valores de control con `paquetes.txt`

| Dato | Valor |
|---|---|
| Líneas leídas / vacías / con error | 174 / 14 / 0 |
| Paquetes procesados | 160 |
| TCP / UDP / ICMP | 104 / 42 / 14 |
| IP origen más frecuente | 192.168.1.1 y 1.1.1.1 (7 cada una, empate) |
| IP destino más frecuente | 93.184.216.34 (14) |
| Puerto destino más frecuente | 25 / SMTP (18 de 146 paquetes TCP+UDP) |
| Flags (combinación) | PSH-ACK 54, SYN 48, ACK 2 |
| Flags (individual) | ACK 56, PSH 54, SYN 48 |
| Consultas / respuestas | 28 / 28 (DNS 8/8, ICMP 7/7, NTP 6/6, SNMP 6/6, DHCP 1/1) |

Criterio de consultas y respuestas: ICMP se lee de su cabecera (tipo 8 = solicitud, 0 = respuesta);
DNS, NTP, SNMP y DHCP se leen de los datos de aplicación según las reglas de cada protocolo.
