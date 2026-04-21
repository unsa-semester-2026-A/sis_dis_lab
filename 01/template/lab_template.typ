#let AbbreviateByCaps(w) = {
  let chars = w.clusters()
  let caps = chars.filter(c => c == upper(c) and c != lower(c))
  caps.join("")
}

#let AbbreviateFullName(name) = {
  let parts = name.split(" ")
  parts.at(0)
  ", "
  parts.at(2)
}

#let genTime = datetime.today()

// any capital letter in the string will be part of the abbreviation
#let courseName = "Sistemas Distribuidos"
#let courseAbbv = AbbreviateByCaps(courseName)

// last names, first names
// just use spaces and at least 3 words or it won't work
// the abbreviation will be "third_word, first_word"
#let memberList = (
  "Barrios Medina, Mathias Alonso",
  "Huacani Jara Denise Andrea",
  "Hancco Mullisaca, Sergio Danilo",
  "Pacheco Palo, Fabiana Francinet",
  "Quispe Condori, Alvaro Raul",
)
#let memberAbbvList = memberList.map(n => AbbreviateFullName(n))

// title
#let labTitle = "Los Hilos (Threads)"

// lab number
#let labNumber = "01"

// instructor name
#let instructorName = "Mg. Maribel Molina Barriga"

// date stuff which doesn't need to be touched
#let year = { genTime.year() }
// could technically be A if month < 7 else B but that depends on uni not delaying classes (always happens)
#let semCode = "A"
#let presentationDate = genTime.display("[day]/[month]/[year]")
#let presentationHour = "11:59:00"


// layout constants
// sizes
#let tableBorderWidth = 0.5pt

#let tableRowMinHeight = 16pt
// colors
#let headerBorderColor = rgb("#808080")
#let tbHeaderBgColor = rgb("#C8310E")
#let codeBgColor = rgb("#F1F3F4")
// fonts
#set text(
  font: "Lato",
)

#let fontBuild(content, weight, size, alignTo, color) = [
  #set text(size: size, weight: weight, fill: color)
  #if alignTo != none [
    #align(alignTo)[#content]
  ] else [
    #content
  ]
]

#let headerBig(content, weight: "regular", alignTo: none, color: black) = fontBuild(
  content,
  weight,
  7.5pt,
  alignTo,
  color,
)
#let headerSmall(content, weight: "regular", alignTo: none, color: black) = fontBuild(
  content,
  weight,
  7pt,
  alignTo,
  color,
)
#let mainTitle(content) = fontBuild(content, "bold", 13pt, center, black)
#let tableTitle(content, weight: "regular", alignTo: none, color: black) = fontBuild(
  content,
  weight,
  11pt,
  alignTo,
  color,
)
#let tableContents(content, weight: "regular", alignTo: none, color: black) = fontBuild(
  content,
  weight,
  8.5pt,
  alignTo,
  color,
)

// technically components
#let ordList(items) = [
  #set list(
    indent: 1em,
    marker: "1.1.",
  )
  #for item in items [
    + #item
  ]
]
#let unordList(items) = [
  #set list(
    indent: 1em,
    marker: "-",
  )
  #for item in items [
    - #item
  ]
]

#let pageHeader = block(
  width: 100%,
  inset: (bottom: 1em),
)[
  #table(
    align: center + horizon,
    stroke: tableBorderWidth + headerBorderColor,
    columns: (1fr, 2fr, 1fr),
    align(horizon)[#image("img/fixed/epis.png", width: 95%)],
    headerBig(weight: "bold")[
      UNIVERSIDAD NACIONAL DE SAN AGUSTÍN \
      FACULTAD DE INGENIERÍA DE PRODUCCIÓN Y SERVICIOS \
      ESCUELA PROFESIONAL DE INGENIERÍA DE SISTEMAS
    ],
    align(horizon)[#image("img/fixed/abet.png", width: 97%)],
    table.cell(colspan: 3)[
      #headerSmall(weight: "bold")[Formato: ]
      #headerSmall[Guía de Práctica de Laboratorio / Talleres / Centros de Simulación]
    ],
    headerSmall(weight: "bold")[Aprobación: 2022/03/01],
    headerSmall(weight: "bold")[Código: GUIA-PRLE-001],
    context headerSmall(weight: "bold", alignTo: right)[Página: #counter(page).display("1")],
  )
]

#set page(
  paper: "a4",
  margin: (
    top: 6cm,
    bottom: 2.54cm,
    left: 1.9cm,
    right: 1.9cm,
  ),
  header: pageHeader,
  header-ascent: 5%,
)

#set document(
  title: upper[#courseAbbv - Laboratorio #labNumber - #memberAbbvList.join(" - ")],
)

#align(center)[#mainTitle[INFORME DE LABORATORIO]]

#table(
  align: left + horizon,
  stroke: black + 1pt,
  inset: 0.5em,
  columns: (1fr, 1fr, 1fr, 1fr, 1fr, 1fr),
  table.cell(
    colspan: 6,
    fill: tbHeaderBgColor,
    tableTitle(weight: "bold", alignTo: center, color: white)[INFORMACIÓN BÁSICA],
  ),
  tableContents(weight: "bold")[ASIGNATURA:],
  table.cell(
    colspan: 5,
    tableContents[#courseName],
  ),
  tableContents(weight: "bold")[TÍTULO DE LA PRÁCTICA:],
  table.cell(
    colspan: 5,
    tableContents[#labTitle],
  ),
  tableContents(weight: "bold")[NÚMERO DE LA PRÁCTICA:],
  tableContents[#labNumber],
  tableContents(weight: "bold")[AÑO LECTIVO:],
  tableContents[#year],
  tableContents(weight: "bold")[NRO. SEMESTRE:],
  tableContents[#semCode],
  tableContents(weight: "bold")[FECHA DE PRESENTACIÓN:],
  tableContents[#presentationDate],
  tableContents(weight: "bold")[HORA DE PRESENTACIÓN:],
  table.cell(
    colspan: 3,
    tableContents[#presentationHour],
  ),
  table.cell(
    colspan: 4,
    [
      #tableContents(weight: "bold")[INTEGRANTE(s):] \
      #tableContents[#unordList(memberList)]

    ],
  ),
  tableContents(weight: "bold")[NOTA:],
  tableContents[],
  table.cell(
    colspan: 6,
    [
      #tableContents(weight: "bold")[DOCENTE: ] \
      #tableContents[#instructorName]
    ],
  ),
)

#let codeBlock(file, lang: "text") = block(
  fill: codeBgColor,
  breakable: true,
  width: 100%,
  inset: 1em,
  radius: 8pt,
)[
  #raw(read(file), lang: lang)
]

// ─── SOLUCIÓN Y RESULTADOS ───────────────────────────────────────────────────
#table(
  align: left + top,
  stroke: black + 0.5pt,
  inset: 1em,
  columns: 1fr,
  table.cell(
    fill: tbHeaderBgColor,
    inset: 0.5em,
    align: center + horizon,
    tableTitle(weight: "bold", color: white)[SOLUCIÓN Y RESULTADOS],
  ),

  // ── I. EJERCICIOS ─────────────────────────────────────────────────────────
  tableContents[
    #set par(justify: true)
    *I. SOLUCIÓN DE EJERCICIOS/PROBLEMAS*

    === Anexo 1 — Caso Estudio: Simulación de cobro en supermercado

    El Anexo 1 plantea simular el proceso de cobro de un supermercado con dos
    clientes y dos cajeras. Se implementaron tres versiones progresivas:

    ==== 1.1. Versión secuencial (sin hilos)

    Las clases `Cliente.java` y `Cajera.java` modelan el dominio básico. La clase
    `Main.java` ejecuta el cobro de forma secuencial: primero se atiende al
    Cliente 1 y, solo cuando termina, se atiende al Cliente 2.

    *Clase `Cliente.java`*
    #codeBlock("./src/lst/anexo1/Cliente.java", lang: "java")

    *Clase `Cajera.java`*
    #codeBlock("./src/lst/anexo1/Cajera.java", lang: "java")

    *Clase `Main.java` (secuencial)*
    #codeBlock("./src/lst/anexo1/Main.java", lang: "java")

    *Salida de ejecución — versión secuencial:*
    #codeBlock("./src/lst/output/main_secuencial.txt")

    Se observa que la Cajera 1 atiende al Cliente 1 completo (15 seg) y recién
    después la Cajera 2 atiende al Cliente 2 (11 seg adicionales). El tiempo
    total es de *26 segundos*.

    ==== 1.2. Versión con hilos — `extends Thread`

    La clase `CajeraThread` hereda de `Thread` y sobre-escribe `run()`, permitiendo
    que ambas cajeras procesen sus compras en paralelo.

    *Clase `CajeraThread.java`*
    #codeBlock("./src/lst/anexo1/CajeraThread.java", lang: "java")

    *Clase `MainThread.java`*
    #codeBlock("./src/lst/anexo1/MainThread.java", lang: "java")

    *Salida de ejecución — `extends Thread`:*
    #codeBlock("./src/lst/output/main_thread.txt")

    Ambas cajeras inician a t=0. La Cajera 2 termina a los 11 seg y la Cajera 1
    a los 15 seg. Tiempo total: *15 segundos* (reducción del 42 % respecto al
    modelo secuencial).

    ==== 1.3. Versión con hilos — `implements Runnable`

    `MainRunnable` implementa la interfaz `Runnable` en lugar de extender `Thread`.
    Esta aproximación es preferible cuando la clase ya hereda de otra, ya que Java
    no soporta herencia múltiple.

    *Clase `MainRunnable.java`*
    #codeBlock("./src/lst/anexo1/MainRunnable.java", lang: "java")

    *Salida de ejecución — `implements Runnable`:*
    #codeBlock("./src/lst/output/main_runnable.txt")

    El resultado es idéntico al de `extends Thread` (15 seg), demostrando que
    ambas estrategias producen el mismo comportamiento concurrente.

    === Anexo 2 — Caso Estudio: Productor–Consumidor

    El Anexo 2 implementa el clásico problema Productor–Consumidor. Se presentan
    dos variantes: sin sincronización (para evidenciar condiciones de carrera) y
    con sincronización correcta mediante `wait()`/`notifyAll()`.

    ==== 2.1. Variante SIN sincronización — condiciones de carrera

    Las clases `CubbyHoleNoSync`, `ProductorNoSync` y `ConsumidorNoSync` operan
    sobre el recurso compartido sin ningún mecanismo de control de acceso.

    *Clase `CubbyHoleNoSync.java`*
    #codeBlock("./src/lst/anexo2/CubbyHoleNoSync.java", lang: "java")

    *Clase `ProductorNoSync.java`*
    #codeBlock("./src/lst/anexo2/ProductorNoSync.java", lang: "java")

    *Clase `ConsumidorNoSync.java`*
    #codeBlock("./src/lst/anexo2/ConsumidorNoSync.java", lang: "java")

    *Clase `DemoNoSync.java`*
    #codeBlock("./src/lst/anexo2/DemoNoSync.java", lang: "java")

    *Salida de ejecución — sin sincronización:*
    #codeBlock("./src/lst/output/demo_nosync.txt")

    Se aprecia con claridad la *condición de carrera*: el Consumidor leyó el
    valor `0` diez veces consecutivas porque el Productor aún no había colocado
    ningún valor nuevo; posteriormente el Productor puso los valores 1–9 sin que
    el Consumidor los llegara a consumir. Este es el problema descrito en la guía
    como "el Consumidor imprimirá el mismo valor dos o más veces".

    ==== 2.2. Variante CON sincronización — `wait()`/`notifyAll()`

    `CubbyHole` utiliza los métodos `synchronized`, `wait()` y `notifyAll()` para
    garantizar que el Consumidor obtenga cada entero producido exactamente una vez.
    El flag `available` coordina el acceso: el Productor espera mientras
    `available == true` y el Consumidor espera mientras `available == false`.

    *Clase `CubbyHole.java`*
    #codeBlock("./src/lst/anexo2/CubbyHole.java", lang: "java")

    *Clase `Productor.java`*
    #codeBlock("./src/lst/anexo2/Productor.java", lang: "java")

    *Clase `Consumidor.java`*
    #codeBlock("./src/lst/anexo2/Consumidor.java", lang: "java")

    *Clase `Demo.java`*
    #codeBlock("./src/lst/anexo2/Demo.java", lang: "java")

    *Salida de ejecución — con sincronización:*
    #codeBlock("./src/lst/output/demo_sync.txt")

    Todos los valores del 0 al 9 son producidos y consumidos exactamente una vez.
    El orden de impresión en consola puede variar (depende del scheduler de la JVM),
    pero los valores nunca se pierden ni se duplican. La sincronización resuelve
    por completo las condiciones de carrera.
  ],

  // ── II. CUESTIONARIO ──────────────────────────────────────────────────────
  tableContents[
    #set par(justify: true)
    *II. SOLUCIÓN DEL CUESTIONARIO*

    ==== a. ¿Por qué es importante el estudio de hilos y multihilos en un sistema distribuido?

    En un sistema distribuido varios nodos cooperan para llevar a cabo una tarea
    común, normalmente a través de una red. En este contexto los hilos son
    fundamentales por las siguientes razones:

    - *Ocultamiento de latencia de red:* las operaciones de E/S bloqueantes
      (envío/recepción de mensajes, lectura de disco) pueden ejecutarse en hilos
      separados de modo que el programa principal no quede paralizado mientras
      espera una respuesta remota #cite(<tanenbaum2008>).

    - *Aprovechamiento de hardware multinúcleo:* los servidores modernos disponen
      de múltiples núcleos; sin hilos un único proceso usaría apenas uno de ellos,
      desperdiciando la mayor parte del hardware disponible #cite(<goetz2006>).

    - *Modelo servidor multihilo:* siguiendo el modelo Dispatcher/Worker, un hilo
      escucha nuevas conexiones y delega cada petición a un hilo trabajador,
      aumentando el throughput sin necesidad de procesos pesados #cite(<tanenbaum2008>).

    - *Escalabilidad y tiempo de respuesta:* en servidores web replicados (p. ej.
      round-robin de carga), cada réplica puede atender peticiones en paralelo con
      hilos, reduciendo el tiempo medio de respuesta percibido por los clientes.

    - *Sincronización de estados compartidos:* los algoritmos de consenso
      distribuido, elección de líder y replicación de datos requieren comunicación
      concurrente entre nodos; los hilos proveen el modelo de concurrencia básico
      sobre el que se construyen esos protocolos.

    ==== b. ¿Cómo están compuestos los hilos? ¿Cuál es la diferencia entre hilos y procesos?

    *Composición de un hilo:*
    Un hilo (thread) es la unidad mínima de ejecución planificada por el sistema
    operativo. Cada hilo posee su propio #cite(<silberschatz2018>):

    - *Contador de programa (PC):* indica la instrucción en curso.
    - *Conjunto de registros:* estado del procesador local al hilo.
    - *Pila de ejecución:* variables locales y marcos de llamada.
    - *Estado de ejecución:* listo, ejecutándose, bloqueado o terminado.

    Los hilos pertenecientes al mismo proceso *comparten* el espacio de
    direcciones, el heap, los archivos abiertos y otros recursos del proceso.

    *Diferencia entre hilos y procesos:*

    #table(
      columns: (auto, 1fr, 1fr),
      stroke: 0.5pt + black,
      inset: 0.5em,
      align: left + horizon,
      table.cell(fill: tbHeaderBgColor)[
        #text(fill: white, weight: "bold", size: 8pt)[Aspecto]
      ],
      table.cell(fill: tbHeaderBgColor)[
        #text(fill: white, weight: "bold", size: 8pt)[Proceso]
      ],
      table.cell(fill: tbHeaderBgColor)[
        #text(fill: white, weight: "bold", size: 8pt)[Hilo]
      ],
      [Espacio de memoria], [Propio (aislado)], [Compartido con el proceso padre],
      [Recursos (archivos, sockets)], [Propios], [Compartidos],
      [Cambio de contexto], [Costoso (guarda/restaura PCB completo)], [Ligero (solo registros y pila)],
      [Creación], [Lenta (fork/exec)], [Rápida],
      [Comunicación], [IPC: pipes, sockets, memoria compartida], [Variables compartidas en memoria],
      [Fallo], [No afecta a otros procesos], [Puede derribar todo el proceso],
      [Protección], [Alta (MMU entre procesos)], [Baja (acceso directo a memoria compartida)],
    )

    ==== c. Cuadro comparativo de ventajas y desventajas del uso de hilos

    #table(
      columns: (1fr, 1fr),
      stroke: 0.5pt + black,
      inset: 0.5em,
      align: left + top,
      table.cell(fill: tbHeaderBgColor)[
        #text(fill: white, weight: "bold", size: 8pt)[Ventajas]
      ],
      table.cell(fill: tbHeaderBgColor)[
        #text(fill: white, weight: "bold", size: 8pt)[Desventajas]
      ],
      [*Menor overhead de creación:* crear un hilo es mucho más rápido que crear un proceso ya que no requiere duplicar el espacio de direcciones.],
      [*Condiciones de carrera:* el acceso concurrente a variables compartidas sin sincronización produce resultados erróneos e impredecibles #cite(<lea1999>).],
      [*Comunicación eficiente:* los hilos comparten memoria directamente sin necesidad de mecanismos costosos de IPC.],
      [*Deadlocks:* dos o más hilos pueden bloquearse mutuamente esperando recursos que el otro retiene.],
      [*Mejor aprovechamiento de CPU:* mientras un hilo espera E/S, otro puede usar el procesador, maximizando el uso del hardware #cite(<goetz2006>).],
      [*Dificultad de depuración:* los errores de concurrencia (race conditions, deadlocks) son no deterministas y difíciles de reproducir.],
      [*Escalabilidad en servidores:* el modelo multihilo permite atender miles de solicitudes concurrentes en servidores web y de bases de datos.],
      [*Propagación de errores:* un hilo que lanza una excepción no controlada puede terminar todo el proceso.],
      [*Paralelismo real en multinúcleo:* con múltiples núcleos, los hilos pueden ejecutarse físicamente en paralelo reduciendo el tiempo de respuesta.],
      [*Complejidad del código:* la sincronización con `synchronized`, `wait()`, `notify()` aumenta la complejidad y el riesgo de errores sutiles.],
      [*Ocultan latencia de red:* en clientes web, un hilo puede descargar imágenes mientras otro renderiza HTML, mejorando la experiencia de usuario.],
      [*Uso excesivo de hilos:* crear demasiados hilos genera overhead de planificación y puede degradar el rendimiento (thrashing de contexto).],
    )
  ],

  // ── III. CONCLUSIONES ─────────────────────────────────────────────────────
  tableContents[
    #set par(justify: true)
    *III. CONCLUSIONES*

    + *Los hilos reducen significativamente el tiempo de ejecución en tareas
      paralelizables.* En la simulación del supermercado, pasar del modelo
      secuencial al multihilo redujo el tiempo de 26 a 15 segundos (−42 %), lo
      que evidencia el beneficio directo de la concurrencia cuando las tareas son
      independientes entre sí.

    + *Java ofrece dos mecanismos equivalentes para crear hilos:* extender `Thread`
      e implementar `Runnable`. Ambos producen el mismo comportamiento en tiempo de
      ejecución; sin embargo, `implements Runnable` es preferible en diseños donde
      la clase ya hereda de otra, respetando el principio de composición sobre
      herencia.

    + *La sincronización es imprescindible en el acceso a recursos compartidos.*
      El experimento Productor–Consumidor sin sincronización demostró con claridad
      las condiciones de carrera: el Consumidor leyó el valor `0` diez veces y los
      valores 1–9 nunca fueron consumidos. Con `synchronized` + `wait()`/`notifyAll()`
      el problema se resolvió completamente, garantizando que cada valor se produce
      y consume exactamente una vez.

    + *Los monitores de Java (`wait`/`notify`) son la primitiva de sincronización
      de alto nivel que encapsula mutex + variable de condición.* Cuando un hilo
      invoca `wait()` libera temporalmente el monitor, permitiendo que otros hilos
      entren al método sincronizado; `notifyAll()` despierta a todos los hilos
      esperando, evitando bloqueos permanentes.

    + *En sistemas distribuidos, los hilos son la base del modelo cliente–servidor
      multihilo,* que permite ocultar la latencia de red, atender múltiples clientes
      simultáneamente y aprovechar arquitecturas multinúcleo, siendo la tecnología
      de concurrencia más extendida en la industria (servidores web, bases de datos,
      middleware de mensajería, etc.).
  ]
)

// ── RETROALIMENTACIÓN ────────────────────────────────────────────────────────
#grid(
  align: left + horizon,
  stroke: black + 1pt,
  inset: 0.5em,
  columns: 1fr,
  grid.cell(
    fill: tbHeaderBgColor,
    tableTitle(weight: "bold", alignTo: center, color: white)[RETROALIMENTACIÓN GENERAL],
  ),
  tableContents[
    #v(6em)
  ]
)

// ── REFERENCIAS ──────────────────────────────────────────────────────────────
#grid(
  align: left + horizon,
  stroke: black + 1pt,
  inset: 0.5em,
  columns: 1fr,
  grid.cell(
    fill: tbHeaderBgColor,
    tableTitle(weight: "bold", alignTo: center, color: white)[REFERENCIAS Y BIBLIOGRAFÍA],
  ),
  tableContents[
    #set par(justify: true)

    #bibliography("refs.bib", style: "ieee", title: none)
  ]
)

#set par(justify: true)
