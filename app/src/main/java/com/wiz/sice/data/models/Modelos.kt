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
    val rawJson: String = ""
)