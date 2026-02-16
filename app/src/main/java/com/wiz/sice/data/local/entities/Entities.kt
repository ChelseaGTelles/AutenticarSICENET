package com.wiz.sice.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val matricula: String,
    val nombre: String,
    val carrera: String,
    val situacion: String,
    val fechaReins: String,
    val modEducativo: String,
    val adeudo: Boolean,
    val urlFoto: String,
    val adeudoDescripcion: String,
    val inscrito: Boolean,
    val estatus: String,
    val semActual: String,
    val cdtosAcumulados: String,
    val cdtosActuales: String,
    val especialidad: String,
    val lineamiento: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "sicenet_data")
data class SicenetDataEntity(
    @PrimaryKey val dataType: String,
    val content: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val matricula: String,
    val contrasenia: String,
    val tipoUsuario: String,
    val isSaved: Boolean = true
)
