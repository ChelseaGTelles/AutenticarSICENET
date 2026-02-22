package com.wiz.sice.data.repository

import com.wiz.sice.data.local.SicenetDatabase
import com.wiz.sice.data.local.entities.*
import com.wiz.sice.data.models.*
import org.json.JSONObject

class LocalRepository(private val database: SicenetDatabase) : LocalRepositoryInterface {

    private val profileDao = database.profileDao()
    private val sessionDao = database.sessionDao()
    private val cargaDao = database.cargaDao()
    private val kardexDao = database.kardexDao()
    private val califUnidadesDao = database.califUnidadesDao()
    private val califFinalesDao = database.califFinalesDao()

    override suspend fun saveProfile(profile: AlumnoProfile) {
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

    override suspend fun getProfile(): AlumnoProfile? {
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

    override suspend fun getProfileLastUpdated(): Long {
        return profileDao.getProfile()?.lastUpdated ?: 0L
    }

    override suspend fun saveCarga(items: List<CargaItem>) {
        val entities = items.map { item ->
            CargaEntity(
                materia = item.Materia,
                grupo = item.Grupo,
                docente = item.Docente,
                creditosMateria = item.CreditosMateria,
                lunes = item.Lunes,
                martes = item.Martes,
                miercoles = item.Miercoles,
                jueves = item.Jueves,
                viernes = item.Viernes
            )
        }
        cargaDao.clearCarga()
        cargaDao.insertAll(entities)
    }

    override suspend fun getCarga(): List<CargaItem>? {
        val entities = cargaDao.getAllCarga()
        return if (entities.isNotEmpty()) {
            entities.map { entity ->
                CargaItem(
                    Materia = entity.materia,
                    Grupo = entity.grupo,
                    Docente = entity.docente,
                    CreditosMateria = entity.creditosMateria,
                    Lunes = entity.lunes,
                    Martes = entity.martes,
                    Miercoles = entity.miercoles,
                    Jueves = entity.jueves,
                    Viernes = entity.viernes
                )
            }
        } else null
    }

    override suspend fun saveKardex(items: List<KardexItem>) {
        val entities = items.map { item ->
            KardexEntity(
                clvOficial = item.clvOficial,
                materia = item.materia,
                periodo = item.periodo,
                promedio = item.promedio
            )
        }
        kardexDao.clearKardex()
        kardexDao.insertAll(entities)
    }

    override suspend fun getKardex(): List<KardexItem>? {
        val entities = kardexDao.getAllKardex()
        return if (entities.isNotEmpty()) {
            entities.map { entity ->
                KardexItem(
                    clvOficial = entity.clvOficial,
                    materia = entity.materia,
                    periodo = entity.periodo,
                    promedio = entity.promedio
                )
            }
        } else null
    }

    override suspend fun saveCalifUnidades(items: List<CalifUnidadItem>) {
        val entities = items.map { item ->
            val unitsJson = JSONObject()
            item.unidades.forEach { (key, value) ->
                unitsJson.put(key.toString(), value)
            }
            CalifUnidadesEntity(
                materia = item.Materia,
                grupo = item.Grupo,
                unidades = unitsJson.toString()
            )
        }
        califUnidadesDao.clearCalifUnidades()
        califUnidadesDao.insertAll(entities)
    }

    override suspend fun getCalifUnidades(): List<CalifUnidadItem>? {
        val entities = califUnidadesDao.getAllCalifUnidades()
        return if (entities.isNotEmpty()) {
            entities.map { entity ->
                val unitsMap = mutableMapOf<Int, String>()
                try {
                    val unitsObj = JSONObject(entity.unidades)
                    unitsObj.keys().forEach { key ->
                        unitsMap[key.toInt()] = unitsObj.getString(key)
                    }
                } catch (_: Exception) { }
                CalifUnidadItem(
                    Materia = entity.materia,
                    Grupo = entity.grupo,
                    unidades = unitsMap
                )
            }
        } else null
    }

    override suspend fun saveCalifFinales(items: List<CalifFinalItem>) {
        val entities = items.map { item ->
            CalifFinalesEntity(
                materia = item.materia,
                calif = item.calif,
                acred = item.acred,
                grupo = item.grupo,
                observaciones = item.Observaciones
            )
        }
        califFinalesDao.clearCalifFinales()
        califFinalesDao.insertAll(entities)
    }

    override suspend fun getCalifFinales(): List<CalifFinalItem>? {
        val entities = califFinalesDao.getAllCalifFinales()
        return if (entities.isNotEmpty()) {
            entities.map { entity ->
                CalifFinalItem(
                    materia = entity.materia,
                    calif = entity.calif,
                    acred = entity.acred,
                    grupo = entity.grupo,
                    Observaciones = entity.observaciones
                )
            }
        } else null
    }

    override suspend fun getDataLastUpdated(type: String): Long {
        return when (type) {
            "CARGA" -> cargaDao.getLastUpdated() ?: 0L
            "KARDEX" -> kardexDao.getLastUpdated() ?: 0L
            "CALIF_UNIDADES" -> califUnidadesDao.getLastUpdated() ?: 0L
            "CALIF_FINALES" -> califFinalesDao.getLastUpdated() ?: 0L
            else -> 0L
        }
    }

    override suspend fun saveSession(matricula: String, contrasenia: String, tipoUsuario: String) {
        sessionDao.insertSession(SessionEntity(matricula = matricula, contrasenia = contrasenia, tipoUsuario = tipoUsuario))
    }

    override suspend fun getSession(): SessionEntity? {
        return sessionDao.getSession()
    }

    override suspend fun clearAll() {
        profileDao.clearProfile()
        sessionDao.clearSession()
        cargaDao.clearCarga()
        kardexDao.clearKardex()
        califUnidadesDao.clearCalifUnidades()
        califFinalesDao.clearCalifFinales()
    }
}


