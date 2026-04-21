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
#let courseName = "Nombre del Curso"
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
#let labTitle = "Título del Laboratorio"

// lab number
#let labNumber = "N"

// instructor name
#let instructorName = "Mg. Maribel Molina Barriga"

// date stuff which doesn't need to be touched
#let year = { genTime.year() }
// could technically be A if month < 7 else B but that depends on uni not delaying classes (always happens)
#let semCode = "B"
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
  tableContents[Nota colocada por el docente],
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

// Ejemplo
// #grid(
//   align: left + horizon,
//   stroke: black + 1pt,
//   inset: 0.5em,
//   columns: 1fr,
//   grid.cell(
//     fill: tbHeaderBgColor,
//     tableTitle(weight: "bold", alignTo: center, color: white)[RESULTADOS Y PRUEBAS],
//   ),
//   tableContents[
//     #set enum(numbering: "1.1.")
//     #set par(justify: true)
//     + PRUEBA DE EJERCICIOS RESUELTOS:
//       + Ejercicio 1: \
//         #codeBlock("./src/lst/A.java", lang: "java")
//         El programa usa threads extendiendo la clase Thread donde cada thread ejecuta un bucle imprimiendo números del aaaa junto con su nombre, al crear dos threads Pepe y Juan ambos se ejecutan intercalando sus salidas
//         #image("./img/fixed/abet.png")
//       + Ejercicio 2: \
//         #codeBlock("./src/lst/A.java", lang: "java")
//         El programa crea una clase que implementa Runnable donde en su método run ejecuta un bucle imprimiendo números del 1 al 5 junto con el nombre del thread, al crear dos threads Ana y Luis ambos se ejecutan intercalando sus salidas
//         #image("./img/fixed/abet.png")
//     // #lorem(10000)
//   ]
// )

// Contenido
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
  tableContents[
    *I. SOLUCIÓN DE EJERCICIOS/PROBLEMAS* \
    
    // Espacio reservado para las soluciones
    #v(4em)
  ],
  tableContents[
    *II. \u{00A0}\u{00A0}SOLUCIÓN DEL CUESTIONARIO*
    
    // Espacio reservado para las respuestas
    #v(4em)
  ],
  tableContents[
    *III. \u{00A0}CONCLUSIONES* \ \
    
    // Espacio reservado para las conclusiones
    #v(4em)
  ]
)

// RETROALIMENTACIÓN
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

// REFERENCIAS
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
  ]
)

#set par(justify: true)
// #lorem(10000)
