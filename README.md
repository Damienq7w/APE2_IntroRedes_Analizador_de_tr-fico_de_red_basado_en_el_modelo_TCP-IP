# Analizador de tráfico de red basado en el modelo TCP/IP

![Java](https://img.shields.io/badge/Java-11%2B-orange)
![Interfaz](https://img.shields.io/badge/interfaz-consola-lightgrey)
![Dependencias](https://img.shields.io/badge/dependencias%20externas-ninguna-brightgreen)

Aplicación de consola en **Java** que lee un archivo de paquetes de red simulados, separa cada
paquete en las capas del modelo **TCP/IP** (acceso a red, Internet, transporte y aplicación),
muestra sus campos de forma ordenada y genera un resumen estadístico del tráfico.

Proyecto desarrollado para la práctica **APE2** de la asignatura **Introducción a Redes**.

---

## Tabla de contenidos

1. [Información académica](#información-académica)
2. [Integrantes](#integrantes)
3. [Objetivos](#objetivos)
4. [Funcionalidades](#funcionalidades)
5. [Requisitos](#requisitos)
6. [Instalación y ejecución](#instalación-y-ejecución)
7. [Uso](#uso)
8. [Formato del archivo de entrada](#formato-del-archivo-de-entrada)
9. [Arquitectura](#arquitectura)
10. [Relación con el modelo TCP/IP](#relación-con-el-modelo-tcpip)
11. [Validación y manejo de errores](#validación-y-manejo-de-errores)
12. [Resultados con el dataset](#resultados-con-el-dataset)
13. [Decisiones de diseño](#decisiones-de-diseño)
14. [Cumplimiento de la guía](#cumplimiento-de-la-guía)
15. [Limitaciones](#limitaciones)
16. [Referencias](#referencias)

---

## Información académica

| | |
|---|---|
| **Universidad** | Universidad Técnica de Ambato (UTA) |
| **Facultad** | Facultad de Ingeniería en Sistemas, Electrónica e Industrial (FISEI) |
| **Carrera** | Software |
| **Asignatura** | Introducción a Redes |
| **Nivel y paralelo** | Tercero "B" |
| **Docente** | Ing. Daniel Maldonado-Ruiz, Ph.D. |
| **Práctica** | APE2 — Analizador de tráfico de red basado en el modelo TCP/IP |
| **Periodo académico** | Agosto – diciembre 2026 |

## Integrantes

| Integrante | Rol en el proyecto |
|---|---|
| Camacho Monta Josue Jampier | Diagramas (UML y flujo) y resultados obtenidos |
| Cunalata Mendoza Damian Alexander | Desarrollo del código y evidencias de ejecución |
| Lucero Urresta Karol Sulamith | Análisis de resultados: capas inferiores y transporte |
| Silva Camuendo Luis Alexander | Análisis de resultados: puertos, aplicación y validación |


## Objetivos

**General.** Desarrollar una aplicación en Java que permita analizar paquetes de red simulados,
identificando y separando las cabeceras correspondientes a las capas del modelo TCP/IP, con el fin
de interpretar la estructura general del tráfico de red desde una perspectiva orientada a objetos.

**Específicos.**
- Identificar las capas del modelo TCP/IP presentes en un paquete simulado, diferenciando sus cabeceras y su contenido.
- Procesar cadenas estructuradas que representen paquetes de red, extrayendo direcciones MAC, direcciones IP, protocolo de transporte y datos de aplicación.
- Diseñar una solución modular en Java aplicando abstracción, separación de responsabilidades y procesamiento por capas.

## Funcionalidades

**Obligatorias (según la guía)**
- Lectura del archivo de paquetes, procesando cada línea de forma independiente.
- Separación de las cabeceras `ETH`, `IP`, `TCP`/`UDP`/`ICMP` y `APP`, y extracción de sus campos clave–valor.
- Presentación de cada paquete organizado por capas.
- Resumen estadístico: total de paquetes; cantidad de TCP, UDP e ICMP; paquetes por protocolo de aplicación; IP origen, IP destino y puerto destino más frecuentes.
- Reporte de líneas mal formadas sin detener la ejecución.

**Adicionales**
- Menú interactivo con validación de entrada.
- Búsqueda de paquetes por IP (origen o destino).
- Filtros por protocolo de transporte y por protocolo de aplicación.
- Exportación del análisis completo a un archivo de texto.
- Validación estricta de campos (formato MAC, IPv4, rangos de TTL, puertos, secuencia y longitud UDP).
- Clasificación de errores en **sintácticos** y **semánticos**.
- Detección de consultas y respuestas (DNS, ICMP, NTP, SNMP y DHCP).
- Conteo de flags TCP, por combinación y por flag individual.
- Resumen por servicio, identificado por puerto bien conocido.

## Requisitos

- **JDK 11 o superior** (verificar con `java -version`).
- **Visual Studio Code** con la extensión *Extension Pack for Java* (opcional: también se puede compilar por línea de comandos).
- No se usan librerías externas, sockets ni captura de tráfico real.

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Damienq7w/APE2_IntroRedes_Analizador_de_tr-fico_de_red_basado_en_el_modelo_TCP-IP.git
```

### 2. Opción A: Visual Studio Code (recomendada)

1. Abrir en VS Code la **carpeta raíz** del repositorio (la que contiene `.vscode/` y `APE2_Analizador/`).
2. Ir a **Ejecutar y depurar** (`Ctrl+Shift+D`) y elegir una configuración:
   - **Analizar paquetes.txt**: analiza el dataset de la práctica.
   - **Probar lineas con error**: analiza `paquetes_prueba.txt`, que contiene líneas mal formadas a propósito.
3. Pulsar ▶ o `F5`. El menú se abre en la **terminal integrada**, donde se escriben las opciones.

También funciona el botón **Run** que aparece sobre el método `main` de `Main.java`: sin argumentos,
el programa localiza automáticamente `APE2_Analizador/recursos/paquetes.txt`.

### 3. Opción B: línea de comandos

Desde la carpeta raíz del repositorio:

```bash
javac -encoding UTF-8 -d APE2_Analizador/bin -sourcepath APE2_Analizador/src APE2_Analizador/src/analizador/app/Main.java
cd APE2_Analizador
java -cp bin analizador.app.Main recursos/paquetes.txt
```

Para probar el manejo de errores: `java -cp bin analizador.app.Main recursos/paquetes_prueba.txt`

## Uso

Al iniciar, el programa analiza el archivo y muestra un resumen de la carga:

```
Archivo analizado ....: recursos/paquetes.txt
Lineas leidas ........: 174
Paquetes validos .....: 160
Lineas vacias ........: 14
Lineas con error .....: 0
```

A continuación presenta el menú:

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

Ejemplo de salida de un paquete (opción 2):

```
================ PAQUETE #1 (linea 1) ================
[CAPA DE ACCESO A RED] Ethernet
   MAC origen .........: AA:BB:CC:DD:EE:01
   MAC destino ........: FF:EE:DD:CC:BB:01
   Tipo de trama ......: IPv4
[CAPA DE INTERNET] IP
   IP origen ..........: 192.168.1.10
   IP destino .........: 93.184.216.34
   TTL ................: 64
   Protocolo ..........: TCP
[CAPA DE TRANSPORTE] TCP
   Tipo de protocolo ..: TCP
   Puerto origen ......: 51514
   Puerto destino .....: 80 (HTTP)
   Secuencia ..........: 1001
   ACK ................: 2001
   Flags ..............: PSH-ACK
[CAPA DE APLICACION] HTTP
   Protocolo ..........: HTTP
   Datos ..............: GET_/index.html
```

> El **número de paquete** no siempre coincide con el **número de línea**: las líneas vacías del
> archivo son separadores y no cuentan como paquetes. Por ejemplo, el paquete #160 está en la línea 174.

### Reportes

La opción 8 exporta el análisis completo (todos los paquetes, el resumen estadístico y las líneas con
error) en la carpeta **`APE2_Analizador/reportes/`**, que se crea automáticamente. El nombre por defecto
es `reporte_analisis.txt`.

## Formato del archivo de entrada

Cada línea representa un paquete con sus cabeceras en el orden del encapsulamiento:

```
ETH{src=<MAC>,dst=<MAC>,type=IPv4}|IP{src=<IP>,dst=<IP>,ttl=<n>,proto=<TCP|UDP|ICMP>}|<TCP|UDP|ICMP>{...}|APP{proto=<protocolo>,data=<datos>}
```

| Cabecera | Campos |
|---|---|
| `ETH` | `src`, `dst`, `type` |
| `IP` | `src`, `dst`, `ttl`, `proto` |
| `TCP` | `sport`, `dport`, `seq`, `ack`, `flags` |
| `UDP` | `sport`, `dport`, `length` |
| `ICMP` | `type`, `code`, `id`, `seq` |
| `APP` | `proto`, `data` (opcional) |

- Los bloques se separan con `|` y los campos con `,`; cada campo es un par `clave=valor`.
- El bloque `APP` es opcional, porque la guía indica que cada paquete trae sus capas "según corresponda".
- Las líneas vacías se consideran separadores y se cuentan aparte.
- Se toleran espacios alrededor de claves y valores, mayúsculas o minúsculas en los nombres, finales de línea CRLF o LF y la marca BOM de UTF-8.

## Arquitectura

### Estructura del repositorio

```
.
├── .vscode/
│   ├── launch.json            configuraciones de ejecucion
│   └── settings.json          carpeta de fuentes y de compilacion
├── APE2_Analizador/
│   ├── recursos/
│   │   ├── paquetes.txt           dataset entregado por el docente
│   │   └── paquetes_prueba.txt    lineas validas y mal formadas para pruebas
│   ├── reportes/                  reportes exportados (opcion 8)
│   └── src/analizador/            codigo fuente (26 clases, 9 paquetes)
├── .gitignore
└── README.md
```

### Paquetes y clases

| Paquete | Clases | Responsabilidad |
|---|---|---|
| `app` | `Main`, `MenuPrincipal` | Arrancar el programa y atender el menú por consola |
| `enums` | `Capa`, `TipoError`, `ServicioConocido` | Valores fijos: capas TCP/IP, tipo de error y puertos bien conocidos |
| `excepciones` | `PaqueteMalFormadoException` | Error de una línea, con su número, motivo y tipo |
| `modelo` | `Cabecera`, `CabeceraEthernet`, `CabeceraIP`, `CabeceraSobreIP`, `CabeceraConPuertos`, `CabeceraTCP`, `CabeceraUDP`, `CabeceraICMP`, `CabeceraAplicacion`, `Paquete` | Representación del paquete y sus cabeceras |
| `lectura` | `LectorArchivo` | Lectura del archivo línea por línea |
| `analisis` | `ParserPaquete`, `ValidadorCampos`, `ResultadoAnalisis`, `AnalizadorTrafico` | Interpretación y validación sintáctica y semántica |
| `estadistica` | `ResumenEstadistico`, `GeneradorEstadisticas` | Cálculo del resumen estadístico |
| `consulta` | `FiltroPaquetes` | Búsquedas y filtros |
| `reporte` | `FormateadorReporte`, `ExportadorReporte` | Construcción de los textos y exportación a archivo |

### Flujo de procesamiento

El análisis se realiza en una sola pasada. Cada clase recibe el resultado de la anterior y cumple
una única responsabilidad:

1. **Lectura.** `LectorArchivo` abre `paquetes.txt` en UTF-8 y devuelve la lista de líneas, sin interpretarlas.
2. **Análisis de cada línea.** `AnalizadorTrafico` recorre las líneas una por una:
   - Las líneas vacías se cuentan como separadores.
   - Las demás se entregan a `ParserPaquete`, que separa los bloques por `|`, extrae los pares `clave=valor` y crea la cabecera que corresponde a cada bloque (`ETH`, `IP`, `TCP`, `UDP`, `ICMP` o `APP`).
   - Cada cabecera se valida con las reglas de `ValidadorCampos` (formato de MAC e IPv4, rangos de TTL y puertos, etc.).
   - Finalmente se comprueba la coherencia entre cabeceras (validación semántica).
   - Si algo falla, la línea se registra como error y el análisis continúa con la siguiente.
3. **Resultado.** `ResultadoAnalisis` reúne los paquetes válidos, las líneas con error y los contadores de líneas.
4. **Estadísticas.** `GeneradorEstadisticas` recorre los paquetes válidos y produce un `ResumenEstadistico` con todos los conteos.
5. **Presentación.** `FormateadorReporte` convierte paquetes, resumen y errores en texto. `MenuPrincipal` muestra ese texto en la consola y `ExportadorReporte` lo guarda en la carpeta `reportes/`, de modo que ambas salidas son idénticas.

### Jerarquía de cabeceras (herencia y composición)

```
Cabecera  (abstracta: nombre, campos clave=valor, validar())
├── CabeceraEthernet            capa de acceso a red
├── CabeceraIP                  capa de Internet
├── CabeceraSobreIP  (abstracta: lo que IP transporta en su carga util)
│   ├── CabeceraConPuertos  (abstracta: puerto origen y destino)
│   │   ├── CabeceraTCP         secuencia, ACK, flags
│   │   └── CabeceraUDP         longitud
│   └── CabeceraICMP            control de la capa de Internet, sin puertos
└── CabeceraAplicacion          protocolo y datos

Paquete  (composicion)
├── CabeceraEthernet            obligatoria
├── CabeceraIP                  obligatoria
├── CabeceraSobreIP             obligatoria (TCP, UDP o ICMP)
└── CabeceraAplicacion          opcional
```

Cada nivel de la jerarquía agrega solo lo que tienen en común sus subclases. Las tres cabeceras
que viajan dentro de IP comparten `CabeceraSobreIP`; dentro de ellas, únicamente TCP y UDP heredan
de `CabeceraConPuertos`, porque ICMP no usa puertos. Así, el propio diseño refleja la teoría:
el puerto identifica un proceso en la capa de transporte, mientras que ICMP es un protocolo de control
de la capa de Internet.

- **Encapsulamiento:** atributos privados, mapas y listas de solo lectura.
- **Herencia:** cada protocolo es una subclase de `Cabecera`.
- **Composición:** `Paquete` se compone de sus cabeceras.
- **Patrón plantilla:** `Cabecera.validar()` comprueba primero los campos obligatorios (lógica común) y luego el formato propio de cada protocolo (`validarFormato()`).

## Relación con el modelo TCP/IP

| Capa TCP/IP | Clase | Información que muestra | Alcance |
|---|---|---|---|
| Acceso a red | `CabeceraEthernet` | MAC origen, MAC destino, tipo de trama | Local: identifica interfaces del enlace actual |
| Internet | `CabeceraIP` | IP origen, IP destino, TTL, protocolo | Entre redes: identifica a los hosts extremos |
| Internet (control) | `CabeceraICMP` | Tipo, código, identificador, secuencia | Mensajes de control encapsulados en IP |
| Transporte | `CabeceraTCP`, `CabeceraUDP` | Puertos; secuencia, ACK y flags (TCP); longitud (UDP) | Extremo a extremo: el puerto identifica el proceso |
| Aplicación | `CabeceraAplicacion` | Protocolo y datos | Procesos que generan y consumen la información |

## Validación y manejo de errores

Cada línea se procesa dentro de su propio bloque `try/catch`: una línea mal formada se registra con
su número, tipo y motivo, y el análisis continúa con la siguiente (opción 7 del menú).

| Tipo | Qué detecta |
|---|---|
| **Sintáctico** | Llave de cierre faltante; cabecera desconocida; número u orden de bloques incorrecto; bloque o campo repetido; campo obligatorio ausente; MAC o IPv4 con formato inválido; TTL fuera de 0–255; puerto fuera de 0–65535; `seq`/`ack` fuera de 32 bits; flag TCP desconocido; longitud UDP menor que 8 bytes |
| **Semántico** | Incoherencia entre cabeceras: `IP proto=TCP` seguido de un bloque `UDP`; `ETH type` distinto de IPv4 cuando la trama transporta IP |

### Archivo de pruebas

`APE2_Analizador/recursos/paquetes_prueba.txt` contiene 21 líneas:

- **5 válidas:** incluyen la marca BOM, espacios extra, nombres en minúsculas, un ICMP sin bloque APP y un `=` dentro del dato.
- **1 vacía.**
- **15 con un error distinto cada una:** el programa los reporta todos y no se detiene.

## Resultados con el dataset

Valores obtenidos con `APE2_Analizador/recursos/paquetes.txt`:

| Métrica | Resultado |
|---|---|
| Líneas leídas / vacías / con error | 174 / 14 / 0 |
| Paquetes procesados | 160 |
| TCP / UDP / ICMP | 104 (65,0 %) / 42 (26,3 %) / 14 (8,8 %) |
| Protocolos de aplicación | SMTP 18, DNS 16, POP3 15, HTTP 14, IMAP 14, PING 14, SSH 14, FTP 13, NTP 12, SNMP 12, HTTPS 8, TLS 8, DHCP 2 |
| IP origen más frecuente | 192.168.1.1 y 1.1.1.1 (7 paquetes cada una, empate) |
| IP destino más frecuente | 93.184.216.34 (14 paquetes) |
| Puerto destino más frecuente | 25 / SMTP (18 de 146 paquetes TCP y UDP) |
| Flags TCP (combinación) | PSH-ACK 54, SYN 48, ACK 2 |
| Flags TCP (individual) | ACK 56, PSH 54, SYN 48 |
| Consultas / respuestas | 28 / 28 (DNS 8/8, ICMP 7/7, NTP 6/6, SNMP 6/6, DHCP 1/1) |

## Decisiones de diseño

- **ICMP pertenece a la capa de Internet.** En el archivo ocupa la posición de transporte, pero ICMP es un protocolo de control que viaja dentro de IP y no usa puertos. Por eso `CabeceraICMP` hereda de `CabeceraSobreIP` (lo que IP transporta en su carga útil, según su campo `proto`) y no de `CabeceraConPuertos`, y su método `getCapa()` devuelve `INTERNET`.
- **Puertos solo en TCP y UDP.** La estadística de puerto destino se calcula únicamente sobre las cabeceras que heredan de `CabeceraConPuertos`.
- **Empates.** Cuando varias IP o puertos comparten la frecuencia máxima, se muestran todos, en el orden en que aparecen en el archivo, en lugar de elegir uno de forma arbitraria.
- **Consultas y respuestas por protocolo.** ICMP se clasifica por el tipo de su cabecera (8 = Echo Request, 0 = Echo Reply); DNS (`QUERY_`/`RESPONSE_`), NTP (`TIME_REQUEST`/`TIME_RESPONSE`), SNMP (`GET_`/`RESPONSE_`) y DHCP (`DISCOVER`/`OFFER`) se clasifican por sus datos de aplicación.
- **Tipos numéricos.** `seq` y `ack` de TCP se guardan como `long`, porque son campos de 32 bits sin signo que pueden superar el máximo de `int`.
- **Separación entre formato y salida.** `FormateadorReporte` construye los textos y la consola y el archivo exportado los reutilizan, por lo que ambos muestran exactamente lo mismo.

## Cumplimiento de la guía

| Requisito de la guía | Dónde se cumple |
|---|---|
| Clase principal | `app.Main` |
| Clases que representan paquetes o cabeceras | Paquete `modelo` |
| Clase de lectura del archivo | `lectura.LectorArchivo` |
| Módulo de análisis | Paquete `analisis` |
| Módulo de estadísticas y reportes | Paquetes `estadistica` y `reporte` |
| Solución no monolítica | El `main` solo crea el menú; la lógica está en 9 paquetes |
| Desarrollado en Java, interfaz por consola | Sí |
| Sin librerías externas de parsing | Solo `java.util` y `java.util.regex` |
| Sin sockets ni captura de tráfico real | Sí |
| Tolerar distintos protocolos | TCP, UDP e ICMP; 13 protocolos de aplicación |
| Reportar líneas mal formadas sin finalizar abruptamente | `AnalizadorTrafico` y opción 7 del menú |

## Limitaciones

- Un valor no puede contener `,` ni `|`, porque son los separadores del formato.
- Solo se reconoce IPv4.
- El análisis se realiza sobre el archivo completo cargado en memoria, lo que es adecuado para el tamaño del dataset de la práctica.

## Referencias
 
[1] D. Maldonado-Ruiz, «Abstracción de capas», presentación de clase, Introducción a Redes, Universidad Técnica de Ambato, Ambato, Ecuador, sep. 2026.
 
[2] D. Maldonado-Ruiz, «Protocolos», presentación de clase, Introducción a Redes, Universidad Técnica de Ambato, Ambato, Ecuador, sep. 2026.
 
[3] D. Maldonado-Ruiz, «Modelos OSI y TCP/IP», presentación de clase, Introducción a Redes, Universidad Técnica de Ambato, Ambato, Ecuador, oct. 2026.
 
[4] J. F. Kurose y K. W. Ross, *Computer Networking: A Top-Down Approach*, 9.ª ed. Hoboken, NJ, EE. UU.: Pearson Education, 2025.

---

*Proyecto académico — Universidad Técnica de Ambato, 2026.*
