#import "./lib.typ": code-block, define, get-var, header-border-color, lab-report, lab-section, table-border-width
#import "./functions.typ": abbreviate-by-caps, summarize-name
#import "@preview/elembic:1.1.1" as e

#show: e.set_(code-block, lang: "python")
#show: e.set_(code-block, prefix: "#")


// Required vars: course_name, lab_title, lab_number, instructor_name, members
// Optional vars: year, presentation_date, course_abbr, shortnames_chain, surnames_chain, sem_code, presentation_hour, wide_lab_number
// Anything else you can use for submission template config

#define("course_name", "Sistemas Distribuidos")
#define("lab_title", "Bases de datos distribuidas")
#define("lab_number", "08")
#define("instructor_name", "Mg. Maribel Molina Barriga")
#define("members", (
  "Barrios Medina, Mathias Alonso",
  "Hancco Mullisaca, Sergio Danilo",
  "Huacani Jara, Denise Andrea",
  "Pacheco Palo, Fabiana Francinet",
  "Quispe Condori, Alvaro Raul",


))

#context {
  define("course_abbr", abbreviate-by-caps(get-var("course_name")))
  define("shortnames_chain", get-var("members").map(name => summarize-name(name)).join("_"))
  define(
    "surnames_chain",
    get-var("members").map(name => summarize-name(name, positions: (0,), separator: "")).join("-"),
  )
  define("wide_lab_number", numbering("001", int(get-var("lab_number"))))
}

#lab-report()[
  #set image(width: 67%)
  #set list(indent: 2pt)
  #show raw.where(block: false): it => box(
    inset: (x: 0.5pt),
  )[#it]

  #lab-section("RESULTADOS Y PRUEBAS")[
    #show heading: set text(weight: "bold")
    #set par(justify: true)

    #[#text(size: 12pt, weight: "bold")[I. DESARROLLO DE EJERCICIOS PROPUESTOS]]

    Repositorio del laboratorio: #link("https://github.com/unsa-semester-2026-A/sis_dis_lab.git")

    #v(0.3em)

    #block(inset: (left: 1em))[
      *Caso de estudio:* FarmaAndes S.A.

      FarmaAndes S.A. es una cadena farmacéutica peruana con centros de distribución
      en Arequipa, Lima y Cusco. Cada sede posee una base de datos local para
      controlar su inventario. Cuando una sucursal solicita medicamentos a otra sede,
      el sistema debe realizar una transacción distribuida para garantizar que:

      1. El inventario se descuente del almacén origen.
      2. El inventario se incremente en el almacén destino.
      3. Ambas operaciones sean atómicas.

      Si una operación falla, toda la transacción debe revertirse.

      *Implementación:*

      Para comenzar con la implementación, lo primero que se hizo fue crear las
      bases de datos para cada sede: `almacen_arequipa` y `almacen_lima`.

      #code-block("src/casoFarmaAndes/scripts_sql/create_db.sql", lang: "sql")

      #figure(
        image("img/lab/casoFarnaAndes/Creacion de Base de datos almacen_arequipa.jpeg", width: 80%),
        caption: [Creación de la base de datos `almacen_arequipa`],
      )

      Luego se creó la tabla `inventario` en ambas bases de datos. Para Arequipa
      los valores iniciales fueron 100 unidades de Paracetamol, 80 de Ibuprofeno y 50
      de Amoxicilina.

      #code-block("src/casoFarmaAndes/scripts_sql/arequipa.sql", lang: "sql")

      Para Lima se utilizó la misma estructura de tabla, pero con valores iniciales
      distintos: 50 de Paracetamol, 30 de Ibuprofeno y 20 de Amoxicilina.

      #code-block("src/casoFarmaAndes/scripts_sql/lima.sql", lang: "sql")

      Las siguientes capturas evidencian la creación de la tabla y el ingreso de
      datos en ambas sedes.

      #figure(
        image("img/lab/casoFarnaAndes/Ejemplo de creacion de la tabla inventario en almacen_lima.jpeg", width: 80%),
        caption: [Creación de la tabla `inventario` en `almacen_lima`],
      )

      #figure(
        image("img/lab/casoFarnaAndes/Agregar datos a la tabla inventario de almacen_lima.jpeg"),
        caption: [Inserción de datos en la tabla `inventario` de `almacen_lima`],
      )

      El mismo procedimiento se repitió para Arequipa. A continuación se verifica
      que los datos se hayan registrado correctamente.

      #figure(
        image("img/lab/casoFarnaAndes/Ver datos en almacen_arequipa con comados SQL.jpeg"),
        caption: [Verificación de datos ingresados en `almacen_arequipa`],
      )

      Para la interacción con el usuario se desarrolló una interfaz web con Flask.
      El módulo `db.py` encapsula la conexión a PostgreSQL y `app.py`
      define las rutas para realizar transferencias y simular fallos.
      A continuación se muestra el código de `app.py` con comentarios que
      explican cada ruta.

      #code-block("src/casoFarmaAndes/python/app.py", lang: "python")

      La aplicación se ejecuta localmente y presenta una interfaz que permite
      seleccionar producto, cantidad, origen y destino.

      #figure(
        image("img/lab/casoFarnaAndes/Ejecucion de app.png"),
        caption: [Ejecución de la aplicación Flask],
      )

      #figure(
        image("img/lab/casoFarnaAndes/Interfaz de inicio.png"),
        caption: [Interfaz web de FarmaAndes — estado inicial de los inventarios],
      )

      Una vez con ambas bases de datos configuradas y la aplicación en
      funcionamiento, se procedió a resolver los siguientes ejercicios.
    ]

    #v(0.3em)

    = 1. Transferencia Exitosa

    #block(inset: (left: 1em))[
      Transferir 20 unidades de Paracetamol desde Arequipa hacia Lima.

      *Actividades:*
      1. Verificar stock disponible .
      2. Iniciar transacción distribuida.
      3. Descontar stock en Arequipa.
      4. Incrementar stock en Lima.
      5. Confirmar cambios (COMMIT global).

      *Implementación con 2PC:*

      El núcleo de la solución es la función `ejecutar_2pc` del módulo
      `transaccion.py`, que implementa el protocolo Two-Phase Commit sobre dos
      conexiones PostgreSQL. La función recibe el producto, la cantidad, las
      sedes origen y destino, y un flag opcional `simular_fallo`.

      #code-block("src/casoFarmaAndes/python/transaccion.py", snippet: "ejecutar_2pc", lang: "python")

      Antes de ejecutar la transferencia se verificó el estado inicial de los
      inventarios:

      #figure(
        image("img/lab/casoFarnaAndes/Datos antes de la transferencia exitosa.png"),
        caption: [Inventarios antes de la transferencia exitosa],
      )

      Luego de ejecutar la transferencia, el log del protocolo 2PC muestra
      ambas fases (PREPARE y COMMIT) y los inventarios reflejan los valores
      actualizados:

      #figure(
        image("img/lab/casoFarnaAndes/Transferencia Exitosa.png"),
        caption: [Resultado de la transferencia exitosa — COMMIT global ejecutado],
      )
    ]

    #v(0.3em)

    = 2. Simulación de Fallo

    #block(inset: (left: 1em))[
      Durante la transferencia, el nodo Lima deja de responder, lo que debe
      provocar un rollback global.

      *Actividades:*
      1. Iniciar transacción.
      2. Descontar stock en Arequipa.
      3. Simular caída de Lima.
      4. Ejecutar rollback en ambos nodos.

      *Implementación:*

      La misma función `ejecutar_2pc` de `transaccion.py` (mostrada en el
      ejercicio anterior) soporta la simulación de fallo mediante el parámetro
      `simular_fallo=True`. Cuando está activo, después de descontar el stock
      en el origen se lanza una excepción que impide la actualización del
      destino y dispara el rollback.

      A continuación se muestra la parte del código que detecta el fallo y
      la sección que ejecuta el rollback:

      #code-block("src/casoFarmaAndes/python/transaccion.py", snippet: "fallo_check", lang: "python")

      Si la excepción se dispara, el flujo salta al bloque `except`, donde
      se ejecuta el rollback en ambas bases de datos:

      #code-block("src/casoFarmaAndes/python/transaccion.py", snippet: "fallo_rollback", lang: "python")

      Esto se invoca desde la ruta `/fallo` de `app.py`, que pasa
      `simular_fallo=True` a `ejecutar_2pc`.

      Antes de la simulación, los inventarios se encontraban en el estado
      posterior a la transferencia exitosa del ejercicio anterior:

      #figure(
        image("img/lab/casoFarnaAndes/Datos antes de la transferencia fallida.png"),
        caption: [Inventarios antes de la simulación de fallo],
      )

      Al intentar la transferencia con el nodo Lima caído, el sistema ejecuta
      el rollback y muestra el error correspondiente:

      #figure(
        image("img/lab/casoFarnaAndes/Transferencia Fallida.png"),
        caption: [Rollback ejecutado tras la caída simulada del nodo Lima],
      )
    ]

    #v(50em)

    #block(inset: (left: 1em))[
      *Caso de estudio:* Sistema Nacional de Bancos Cooperativos

      Una red financiera opera en tres ciudades: Arequipa, Cusco y Trujillo.
      Cada ciudad administra cuentas locales. Un cliente solicita transferir
      S/ 25 000 desde Arequipa hacia Cusco.

      *Restricciones:*
      - Debe aplicarse atomicidad.
      - Debe garantizarse consistencia.
      - Debe existir recuperación ante fallos.
      - Debe documentarse el proceso mediante 2PC.
    ]

    #v(0.3em)

    = Implementación del caso Banco Cooperativo

    #block(inset: (left: 1em))[

      #v(0.3em)

      *Actividad 1: Diseñar el modelo distribuido.*

      El modelo distribuido del Sistema Nacional de Bancos Cooperativos está
      compuesto por tres nodos PostgreSQL independientes, cada uno alojado en
      una ciudad distinta: `banco_arequipa`, `banco_cusco` y `banco_trujillo`.
      La aplicación Flask actúa como orquestador, comunicándose con los tres
      nodos para ejecutar transacciones distribuidas bajo el protocolo 2PC.
      Cada nodo opera de forma autónoma y solo se sincroniza durante la
      transacción.

      #figure(
        image("img/lab/casoBancoCooperativo/DiagramaDeArquitectura.png", width: 78%),
        caption: [Diagrama de arquitectura del sistema distribuido — 3 nodos],
      )

      Luego de ver la arquitectura, notaremos que para la transaccion si bien son 3 nodos, solo 2 son necesarios para hacer la transaccion quedando el nodo del banco de Trujillo a la espera (Nodo disponible).

      *Actividad 2: Identificar:*
      - *Coordinador:* La aplicación Flask (`app.py`) que ejecuta el protocolo
        2PC, gestiona las fases PREPARE y COMMIT, y decide el resultado global
        de la transacción.
      - *Participantes:* Los tres nodos PostgreSQL: `banco_arequipa` (origen),
        `banco_cusco` (destino) y `banco_trujillo` (nodo disponible).
      - *Recursos involucrados:* Tabla `cuentas` en cada base de datos con los
        campos `id`, `titular` y `saldo`; conexiones entre la aplicación
        Flask y cada nodo PostgreSQL.

      *Actividad 3: Elaborar el diagrama de secuencia del protocolo 2PC.*

      El siguiente diagrama de flujo ilustra las dos fases del protocolo
      Two-Phase Commit: en la Fase 1 (PREPARE) el coordinador consulta a cada
      participante si está listo para confirmar; en la Fase 2 (COMMIT/ABORT)
      el coordinador notifica la decisión global.

      #figure(
        image("img/lab/casoBancoCooperativo/DiagramaDeFlujo.png", width: 78%),
        caption: [Diagrama de flujo del protocolo Two-Phase Commit],
      )

      *Actividad 4: Implementar la simulación usando PostgreSQL.*

      *Creación de las bases de datos y tablas.*

      Para comenzar se crearon las bases de datos para cada sede:
      `banco_arequipa`, `banco_cusco` y `banco_trujillo`. Cada una representa
      un nodo PostgreSQL independiente. El siguiente comando SQL crea la base
      de datos y la tabla `cuentas` en Arequipa con sus valores iniciales.

      #code-block("src/casoBancoCooperativo/scripts_sql/banco_arequipa.sql", lang: "sql")

      #figure(
        image("img/lab/casoBancoCooperativo/Creacion de base de datos sobre bancos.png", width: 45%),
        caption: [Creación de las bases de datos de Arequipa],
      )

      La estructura de la tabla `cuentas` es idéntica en las tres sedes,
      variando únicamente el titular y el saldo inicial. Para Cusco y
      Trujillo se usó la misma definición SQL con otros valores.

      #code-block("src/casoBancoCooperativo/scripts_sql/banco_cusco.sql", lang: "sql")

      #code-block("src/casoBancoCooperativo/scripts_sql/banco_trujillo.sql", lang: "sql")

      #figure(
        image("img/lab/casoBancoCooperativo/ComandoSQLparaMostrarDatosDeLaTabla.png"),
        caption: [Verificación de datos en las tablas de los bancos con `SELECT * FROM cuentas`],
      )

      *Módulo transaccion.py*

      El archivo `transaccion.py` implementa las operaciones del protocolo
      2PC. Incluye funciones auxiliares para conectar y consultar los nodos,
      y las 4 funciones principales del protocolo.

      *Funciones auxiliares*

      `obtener_cuentas` — consulta los saldos de todos los titulares con
      `SELECT titular, saldo FROM cuentas`:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "obtener_cuentas", lang: "python")

      `obtener_titulares` — lista los nombres de los titulares desde Arequipa:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "obtener_titulares", lang: "python")

      `_conectar_par` — abre conexiones a dos nodos con `autocommit` desactivado
      para control manual de `COMMIT` y `ROLLBACK`:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "conectar_par", lang: "python")

      `_obtener_cuenta` — obtiene la primera cuenta disponible con
      `SELECT id, titular, saldo FROM cuentas LIMIT 1`:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "obtener_cuenta", lang: "python")

      *Funciones del protocolo 2PC*

      `ejecutar_2pc` — gestiona la transferencia exitosa con `SELECT`,
      `UPDATE` y `COMMIT`/`ROLLBACK` según el resultado:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "ejecutar_2pc", lang: "python")

      `ejecutar_fallo_red` — simula un timeout en la red durante el `PREPARE`;
      el destino nunca se modifica y se ejecuta `ROLLBACK`:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "fallo_red", lang: "python")

      `ejecutar_caida_nodo` — el destino cae tras el débito en Fase 2; se
      ejecuta `ROLLBACK` para revertir el saldo en origen:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "caida_nodo", lang: "python")

      `ejecutar_recuperacion` — reintenta la transacción completa una vez
      que ambos nodos están en línea:

      #code-block("src/casoBancoCooperativo/python/transaccion.py", snippet: "recuperacion", lang: "python")

      *Coordinador Flask*

      La aplicación `app.py` expone rutas HTTP que invocan las funciones
      anteriores. Cada ruta se comenta con las operaciones SQL que desencadena:

      #code-block("src/casoBancoCooperativo/python/db.py", lang: "python")

      #code-block("src/casoBancoCooperativo/python/app.py", lang: "python")

      #figure(
        image("img/lab/casoBancoCooperativo/Ejecucion de app.png"),
        caption: [Ejecución de la aplicación Flask — coordinador del sistema distribuido],
      )


      *Actividad 5: Simular.*

      *5.1 Transferencia Exitosa*

      Transferir S/ 25 000 desde la cuenta de Alvaro Quispe (Arequipa) hacia
      la cuenta de Fabiana Pacheco (Cusco) mediante el protocolo 2PC.
      (Ver detalle del código en Actividad 4, función `ejecutar_2pc`.)

      #block(inset: (left: 1em))[
        *Actividades:*
        1. Verificar saldo disponible.
        2. Iniciar transacción distribuida (Fase 1: PREPARE).
        3. Debitar S/ 25 000 en Arequipa.
        4. Acreditar S/ 25 000 en Cusco.
        5. Confirmar cambios (Fase 2: COMMIT global).
      ]

      Antes de ejecutar la transferencia se verificó el estado inicial de los
      saldos:

      #figure(
        image("img/lab/casoBancoCooperativo/DatosAntesDeTransaccionExitosa.png"),
        caption: [Saldos antes de la transferencia exitosa],
      )

      Luego de ejecutar la transferencia, el log del protocolo 2PC muestra
      ambas fases (PREPARE y COMMIT) y los saldos reflejan los valores
      actualizados:

      #figure(
        image("img/lab/casoBancoCooperativo/TransferenciaExitosa.png"),
        caption: [Resultado de la transferencia exitosa — COMMIT global ejecutado],
      )

      *5.2 Simulación de Falla de Red*

      Durante la Fase 1 (PREPARE), el nodo destino (Cusco) no responde por
      una falla de red simulada. El coordinador detecta el timeout y ejecuta
      rollback sin haber modificado ningún saldo.
      (Ver detalle del código en Actividad 4, función `ejecutar_fallo_red`.)

      #block(inset: (left: 1em))[
        *Actividades:*
        1. Iniciar transacción.
        2. Verificar saldo en origen (Arequipa).
        3. Simular timeout de red hacia Cusco.
        4. Ejecutar rollback — ningún saldo fue modificado.
      ]

      Antes de la simulación, los saldos se encontraban en el estado posterior
      a la transferencia exitosa del ejercicio anterior:

      #figure(
        image("img/lab/casoBancoCooperativo/DatosAntesDeFallaDeRed.png"),
        caption: [Saldos antes de la simulación de falla de red],
      )

      Al intentar la transferencia con la red caída, el sistema aborta la
      operación sin modificar ningún saldo:

      #figure(
        image("img/lab/casoBancoCooperativo/FalloDeRed.png"),
        caption: [Rollback ejecutado tras falla de red — ningún saldo modificado],
      )

      *5.3 Simulación de Caída de Nodo*

      Durante la Fase 2 (COMMIT), después de haber debitado en origen, el
      nodo destino (Cusco) deja de responder. El coordinador ejecuta rollback
      para revertir el débito y mantener la consistencia.
      (Ver detalle del código en Actividad 4, función `ejecutar_caida_nodo`.)

      #block(inset: (left: 1em))[
        *Actividades:*
        1. Iniciar transacción.
        2. Verificar saldo en origen.
        3. Debitar en Arequipa (PREPARE exitoso).
        4. Simular caída de Cusco durante el COMMIT.
        5. Ejecutar rollback — débito revertido.
      ]

      Antes de la simulación, los saldos estaban en el estado posterior a la
      transferencia exitosa:

      #figure(
        image("img/lab/casoBancoCooperativo/DatosAntesDeCaidaDeNodo.png"),
        caption: [Saldos antes de la simulación de caída de nodo],
      )

      Al caer el nodo destino, el sistema revierte el débito en origen:

      #figure(
        image("img/lab/casoBancoCooperativo/CaidaDeNodo.png"),
        caption: [Rollback ejecutado tras caída de nodo — débito revertido],
      )

      *5.4 Recuperación Posterior*

      Luego de una falla (de red o de nodo), el sistema puede recuperarse
      re-ejecutando la transacción pendiente. Se verifica que ambos nodos
      estén en línea y se completa la operación.
      (Ver detalle del código en Actividad 4, función `ejecutar_recuperacion`.)

      #block(inset: (left: 1em))[
        *Actividades:*
        1. Verificar estado de los nodos (origen y destino en línea).
        2. Consultar log de transacciones pendientes.
        3. Re-ejecutar PREPARE (Fase 1).
        4. Ejecutar COMMIT global (Fase 2).
      ]

      Recuperación tras una falla de red — fallo original:

      #figure(
        image("img/lab/casoBancoCooperativo/FalloDeRed.png"),
        caption: [Falla de red original — rollback ejecutado, saldos intactos],
      )

      Una vez restaurada la conectividad, el coordinador re-ejecuta la
      transacción pendiente con éxito:

      #figure(
        image("img/lab/casoBancoCooperativo/RecuperacionLuegoDeFalloDeRed.png", width: 80%),
        caption: [Recuperación exitosa luego de una falla de red],
      )

      Recuperación tras una caída de nodo — fallo original:

      #figure(
        image("img/lab/casoBancoCooperativo/CaidaDeNodo.png"),
        caption: [Caída de nodo original — débito revertido, estado consistente],
      )

      Tras reiniciar el nodo caído, el coordinador completa la transacción:

      #figure(
        image("img/lab/casoBancoCooperativo/RecuperacionDeCaidaDeNodo.png", width: 80%),
        caption: [Recuperación exitosa luego de una caída de nodo],
      )

      #v(19em)

      *Actividad 6: Analizar.*

      La siguiente tabla resume el impacto del protocolo 2PC sobre las
      dimensiones críticas del sistema y las estrategias aplicadas.

      #table(
        columns: (auto, 2.5fr, 2.5fr),
        align: (left, left, left),
        inset: 8pt,
        stroke: 0.5pt,
        [*Dimensión*], [*Impacto observado*], [*Estrategias aplicadas*],
        [Consistencia],
        [
        El protocolo 2PC garantiza consistencia fuerte: todos los nodos
        reflejan el mismo estado al final. Sin embargo, durante la ventana
        entre PREPARE y COMMIT los recursos quedan bloqueados.
        ],
        [
        - Log de transacciones persistente en cada nodo
        - COMMIT en dos fases para evitar escrituras parciales
        - Rollback automático ante fallo detectado
        ],
        [Disponibilidad],
        [
        El coordinador es punto único de fallo. Si falla durante la Fase 2,
        los participantes quedan en estado incierto hasta su recuperación.
        Una caída de nodo detiene las operaciones del sistema.
        ],
        [
        - Coordinador con failover manual
        - Timeouts configurables (0.3 s) para detectar fallos de red
        - Recuperación posterior re-ejecutando la transacción pendiente
        ],
        [Recuperación],
        [
        El sistema se recupera re-ejecutando la transacción desde el log.
        Si el log no es persistente, se pierde el contexto del fallo y se
        requiere intervención manual.
        ],
        [
        - Función `ejecutar_recuperacion` con reintento completo del 2PC
        - Verificación de estado de nodos antes de reintentar
        - Limpieza del estado de fallo tras recuperación exitosa
        ],
      )
    ]

    #v(0.3em)

    ]

  #v(0.3em)

  #lab-section("CUESTIONARIO")[
    #show heading: set text(weight: "bold")
    #set par(justify: true)

    #[#text(size: 12pt, weight: "bold")[II. CUESTIONARIO]]

    = 1. Una empresa financiera prioriza la disponibilidad del servicio sobre la consistencia de los datos. ¿Qué riesgos podrían surgir y cómo afectarían a los clientes?

    #v(0.3em)

    #block(inset: (left: 1em))[
      Una empresa financiera que prioriza la disponibilidad sobre la consistencia
      (modelo AP del teorema CAP @brewer2000cap) expone los datos a lecturas inconsistentes. Por
      ejemplo, un cliente consulta su saldo justo después de un retiro y aún ve el
      monto anterior, o una transferencia entre cuentas se registra en una pero no
      en la otra.

      *Riesgos concretos:* pagos duplicados, sobregiros no detectados, registros
      contables desincronizados. Los clientes pueden creer que tienen fondos que
      ya no están disponibles, generando rechazos en transacciones posteriores,
      cargos por sobregiro inesperados o pérdida de confianza en la entidad.
    ]

    = 2. El protocolo Two-Phase Commit garantiza consistencia, pero puede reducir la disponibilidad del sistema. ¿Considera que este sacrificio es justificable en todos los contextos empresariales? Fundamente su respuesta.

    #v(0.3em)

    #block(inset: (left: 1em))[
      Two-Phase Commit (2PC @bernshtein1987twoPC) garantiza que todos los nodos confirmen o aborten
      una transacción atómicamente, pero bloquea recursos durante la Coordinación.
      Si el coordinador falla, los nodos quedan bloqueados hasta que se recupere,
      reduciendo la disponibilidad.

      Este sacrificio _no_ es justificable en todos los contextos. Es aceptable en
      sistemas financieros donde una transferencia bancaria debe ser exacta
      (consistencia fuerte). Sin embargo, en redes sociales o e-commerce, una
      transacción parcial (consistencia eventual) es preferible a denegar el
      servicio. Ejemplo: si un banco usa 2PC y el coordinador cae, el cajero
      automático no puede procesar retiros; un sistema AP simplemente mostraría
      "saldo actualizado en breve" y permitiría la operación.
    ]

    = 3. Imagine que una organización global opera cientos de nodos distribuidos. ¿Qué alternativas al protocolo 2PC podrían mejorar el rendimiento sin comprometer significativamente la confiabilidad del sistema?

    #v(0.3em)

    #block(inset: (left: 1em))[
      Existen alternativas al 2PC que mejoran el rendimiento en entornos con
      cientos de nodos:

      - *Saga Pattern @garcia1982saga:* divide la transacción en pasos locales con eventos
        compensatorios. Si un paso falla, se ejecutan las compensaciones de los
        pasos anteriores. No bloquea recursos, pero requiere lógica de
        reversión. Ideal para microservicios.

      - *Consenso distribuido (Raft @ongaro2014raft / Paxos @lamport2001paxos):* los nodos acuerdan un orden de
        operaciones sin un coordinador central frágil. Soportan fallos de
        hasta minority de nodos sin perder disponibilidad.

      - *Base de datos distribuidas con consistencia eventual (Cassandra,
        DynamoDB @decandia2007dynamo):* usan replicación asíncrona y resolución de conflictos
        mediante vectores de versiones. Sacrifican consistencia inmediata por
        alta disponibilidad y escalabilidad lineal.

      - *Protocolo TCC (Try-Confirm/Cancel):* similar a Saga, pero cada
        recurso reserva el cambio en una fase Try y luego Confirma o Cancela.
        Usado en sistemas de pago donde se necesita consistencia sin bloqueos
        largos.
    ]
  ]

  #v(0.3em)

  #lab-section("CONCLUSIONES")[
    #show heading: set text(weight: "bold")
    #set par(justify: true)

    #[#text(size: 12pt, weight: "bold")[III. CONCLUSIONES]]

    #block(inset: (left: 1em))[
      El equilibrio entre consistencia y disponibilidad no es técnico, es humano @tanenbaum2007distributed. Los sistemas financieros que priorizan la consistencia (CP) protegen al usuario de errores económicos, mientras que los que priorizan la disponibilidad (AP) lo protegen de la frustración de un servicio caído. No hay protocolo universal: 2PC ofrece fiabilidad pero centraliza el riesgo en el coordinador; Sagas y TCC delegan la responsabilidad en compensaciones locales, siendo más ágiles pero más complejos de diseñar. Elegir entre ellos es decidir qué error estamos dispuestos a que el usuario experimente.
    ]

    #block(inset: (left: 1em))[
      La tecnología distribuida avanza hacia modelos más resilientes, pero ningún protocolo reemplaza el criterio de dominio. Mientras 2PC y Raft garantizan un estado global predecible, las arquitecturas basadas en eventos (Sagas, DynamoDB) abrazan la incertidumbre y la resuelven después. La decisión final no es técnica: es entender que detrás de cada transacción hay una persona esperando una respuesta, y el sistema debe estar diseñado para no fallarle dos veces.
    ]
  ]

  #v(0.3em)

  #lab-section("REFERENCIAS")[
    #show heading: set text(weight: "bold")
    #bibliography("./bibliography.bib", style: "ieee", title: "Bibliografía")
  ]
]
