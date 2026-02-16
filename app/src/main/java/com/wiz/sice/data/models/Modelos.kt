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