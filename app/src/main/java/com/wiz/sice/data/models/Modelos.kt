package com.wiz.sice.data.models

data class LoginResult(
    val acceso: Boolean,
    val mensaje: String,
    val rawResponse: String? = null
)

data class AlumnoProfile(
    val matricula: String = "",
    val nombre: String = "",
    val carrera: String = "",
    val situacion: String = "",
    val fechaReins: String = "",
    val modEducativo: String = "",
    val adeudo: Boolean = false,
    val urlFoto: String = "",
    val adeudoDescripcion: String = "",
    val inscrito: Boolean = false,
    val estatus: String = "",
    val semActual: String = "",
    val cdtosAcumulados: String = "",
    val cdtosActuales: String = "",
    val especialidad: String = "",
    val lineamiento: String = "",
    val rawJson: String = ""
)

data class CargaItem(
    val Materia: String = "",
    val Grupo: String = "",
    val Docente: String = "",
    val CreditosMateria: Int = 0,
    val Lunes: String = "",
    val Martes: String = "",
    val Miercoles: String = "",
    val Jueves: String = "",
    val Viernes: String = "",
)

data class KardexItem(
    val clvOficial: String = "",
    val materia: String = "",
    val periodo: String = "",
    val promedio: String = ""
)

data class CalifUnidadItem(
    val Materia: String = "",
    val unidades: Map<Int, String> = emptyMap(),
    val Grupo: String = ""
)

data class CalifFinalItem(
    val materia: String = "",
    val calif: String = "",
    val acred: String = "",
    val grupo: String = "",
    val Observaciones: String = ""
)
