package com.wiz.sice.data.repository

import com.wiz.sice.data.local.SicenetDatabase
import com.wiz.sice.data.local.entities.ProfileEntity
import com.wiz.sice.data.local.entities.SessionEntity
import com.wiz.sice.data.local.entities.SicenetDataEntity
import com.wiz.sice.data.models.AlumnoProfile
import com.wiz.sice.data.models.CargaItem
import com.wiz.sice.data.models.KardexItem
import com.wiz.sice.data.models.CalifUnidadItem
import com.wiz.sice.data.models.CalifFinalItem
import org.json.JSONArray
import org.json.JSONObject

class LocalRepository(private val database: SicenetDatabase) {

    private val profileDao = database.profileDao()
    private val dataDao = database.sicenetDataDao()
    private val sessionDao = database.sessionDao()

    suspend fun saveProfile(profile: AlumnoProfile) {
        profileDao.insertProfile(
            ProfileEntity(
                matricula = profile.matricula,
                nombre = profile.nombre,
                carrera = profile.carrera,
                situacion = profile.situacion,
                fechaReins = profile.fechaReins,
                modEducativo = profile.modEducativo,
                adeudo = profile.adeudo,
                urlFoto = profile.urlFoto,
                adeudoDescripcion = profile.adeudoDescripcion,
                inscrito = profile.inscrito,
                estatus = profile.estatus,
                semActual = profile.semActual,
                cdtosAcumulados = profile.cdtosAcumulados,
                cdtosActuales = profile.cdtosActuales,
                especialidad = profile.especialidad,
                lineamiento = profile.lineamiento
            )
        )
    }

    suspend fun getProfile(): AlumnoProfile? {
        return profileDao.getProfile()?.let {
            AlumnoProfile(
                matricula = it.matricula,
                nombre = it.nombre,
                carrera = it.carrera,
                situacion = it.situacion,
                fechaReins = it.fechaReins,
                modEducativo = it.modEducativo,
                adeudo = it.adeudo,
                urlFoto = it.urlFoto,
                adeudoDescripcion = it.adeudoDescripcion,
                inscrito = it.inscrito,
                estatus = it.estatus,
                semActual = it.semActual,
                cdtosAcumulados = it.cdtosAcumulados,
                cdtosActuales = it.cdtosActuales,
                especialidad = it.especialidad,
                lineamiento = it.lineamiento,
                rawJson = ""
            )
        }
    }

    suspend fun getProfileLastUpdated(): Long {
        return profileDao.getProfile()?.lastUpdated ?: 0L
    }

    suspend fun saveData(type: String, content: String) {
        dataDao.insertData(SicenetDataEntity(dataType = type, content = content))
    }

    suspend fun getData(type: String): String? {
        return dataDao.getDataByType(type)?.content
    }

    suspend fun getDataLastUpdated(type: String): Long {
        return dataDao.getDataByType(type)?.lastUpdated ?: 0L
    }

    suspend fun saveSession(matricula: String, contrasenia: String, tipoUsuario: String) {
        sessionDao.insertSession(SessionEntity(matricula = matricula, contrasenia = contrasenia, tipoUsuario = tipoUsuario))
    }

    suspend fun getSession(): SessionEntity? {
        return sessionDao.getSession()
    }

    suspend fun clearAll() {
        profileDao.clearProfile()
        dataDao.clearAllData()
        sessionDao.clearSession()
    }
}
