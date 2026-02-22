package com.wiz.sice.data.repository

import com.wiz.sice.data.local.entities.SessionEntity
import com.wiz.sice.data.models.*

interface LocalRepositoryInterface {
    suspend fun saveProfile(profile: AlumnoProfile)
    suspend fun getProfile(): AlumnoProfile?
    suspend fun getProfileLastUpdated(): Long


    suspend fun saveCarga(items: List<CargaItem>)
    suspend fun getCarga(): List<CargaItem>?


    suspend fun saveKardex(items: List<KardexItem>)
    suspend fun getKardex(): List<KardexItem>?


    suspend fun saveCalifUnidades(items: List<CalifUnidadItem>)
    suspend fun getCalifUnidades(): List<CalifUnidadItem>?


    suspend fun saveCalifFinales(items: List<CalifFinalItem>)
    suspend fun getCalifFinales(): List<CalifFinalItem>?


    suspend fun getDataLastUpdated(type: String): Long


    suspend fun saveSession(matricula: String, contrasenia: String, tipoUsuario: String)
    suspend fun getSession(): SessionEntity?


    suspend fun clearAll()
}

