package com.wiz.sice.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.wiz.sice.data.AccesoLoginRequest
import com.wiz.sice.data.local.SicenetDatabase
import com.wiz.sice.data.repository.LocalRepository
import com.wiz.sice.data.repository.SicenetRepository
import org.json.JSONArray

class Sincronizar(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_TYPE = "KEY_TYPE"
        const val KEY_CONTENT = "KEY_CONTENT"
    }

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val repository = SicenetRepository()
        val localRepository = LocalRepository(SicenetDatabase.getDatabase(applicationContext))

        return try {
            val content = when (type) {
                "PROFILE" -> {
                    val session = localRepository.getSession() ?: return Result.failure()
                    val loginRes = repository.accesoLogin(
                        AccesoLoginRequest(session.matricula, session.contrasenia, session.tipoUsuario)
                    )
                    if (loginRes.isSuccess && loginRes.getOrNull()?.acceso == true) {
                        repository.getAlumno().getOrNull()?.rawJson
                    } else null
                }
                "CARGA" -> {
                    val items = repository.getCargaAcademicaByAlumno().getOrNull() ?: return Result.failure()
                    val json = JSONArray()
                    items.forEach { item ->
                        json.put(org.json.JSONObject().apply {
                            put("Materia", item.Materia)
                            put("Grupo", item.Grupo)
                            put("Docente", item.Docente)
                            put("CreditosMateria", item.CreditosMateria)
                            put("Lunes", item.Lunes)
                            put("Martes", item.Martes)
                            put("Miercoles", item.Miercoles)
                            put("Jueves", item.Jueves)
                            put("Viernes", item.Viernes)
                        })
                    }
                    json.toString()
                }
                "KARDEX" -> {
                    val lineamiento = inputData.getInt("lineamiento", 3)
                    val items = repository.getAllKardexConPromedioByAlumno(lineamiento).getOrNull()
                        ?: return Result.failure()
                    val json = JSONArray()
                    items.forEach { item ->
                        json.put(org.json.JSONObject().apply {
                            put("clvOficial", item.clvOficial)
                            put("materia", item.materia)
                            put("periodo", item.periodo)
                            put("promedio", item.promedio)
                        })
                    }
                    json.toString()
                }
                "CALIF_UNIDADES" -> {
                    val items = repository.getCalifUnidadesByAlumno().getOrNull()
                        ?: return Result.failure()
                    val json = JSONArray()
                    items.forEach { item ->
                        json.put(org.json.JSONObject().apply {
                            put("Materia", item.Materia)
                            put("Grupo", item.Grupo)
                            val unitsObj = org.json.JSONObject()
                            item.unidades.forEach { (key, value) ->
                                unitsObj.put(key.toString(), value)
                            }
                            put("unidades", unitsObj)
                        })
                    }
                    json.toString()
                }
                "CALIF_FINALES" -> {
                    val modEducativo = inputData.getInt("modEducativo", 2)
                    val items = repository.getAllCalifFinalByAlumnos(modEducativo).getOrNull()
                        ?: return Result.failure()
                    val json = JSONArray()
                    items.forEach { item ->
                        json.put(org.json.JSONObject().apply {
                            put("materia", item.materia)
                            put("calif", item.calif)
                            put("acred", item.acred)
                            put("grupo", item.grupo)
                            put("Observaciones", item.Observaciones)
                        })
                    }
                    json.toString()
                }
                else -> null
            }
            
            if (content != null) {
                Log.d("Sincronizar", "Fetched $type successfully")
                val outputData = workDataOf(
                    KEY_CONTENT to content,
                    KEY_TYPE to type
                )
                Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("Sincronizar", "Error fetching $type", e)
            Result.failure()
        }
    }
}

class GuardarLocal(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_TYPE = "KEY_TYPE"
        const val KEY_CONTENT = "KEY_CONTENT"
    }

    override suspend fun doWork(): Result {
        val content = inputData.getString(KEY_CONTENT) ?: return Result.failure()
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val localRepository = LocalRepository(SicenetDatabase.getDatabase(applicationContext))

        return try {
            when (type) {
                "PROFILE" -> {
                    val json = org.json.JSONObject(content)
                    val profile = com.wiz.sice.data.models.AlumnoProfile(
                        matricula = json.optString("matricula"),
                        nombre = json.optString("nombre"),
                        carrera = json.optString("carrera"),
                        situacion = json.optString("situacion"),
                        fechaReins = json.optString("fechaReins"),
                        modEducativo = json.optString("modEducativo"),
                        adeudo = json.optBoolean("adeudo"),
                        urlFoto = json.optString("urlFoto"),
                        adeudoDescripcion = json.optString("adeudoDescripcion"),
                        inscrito = json.optBoolean("inscrito"),
                        estatus = json.optString("estatus"),
                        semActual = json.optString("semActual"),
                        cdtosAcumulados = json.optString("cdtosAcumulados"),
                        cdtosActuales = json.optString("cdtosActuales"),
                        especialidad = json.optString("especialidad"),
                        lineamiento = json.optString("lineamiento"),
                        rawJson = content
                    )
                    localRepository.saveProfile(profile)
                }
                "CARGA" -> {
                    val array = JSONArray(content)
                    val items = List(array.length()) { i ->
                        val json = array.getJSONObject(i)
                        com.wiz.sice.data.models.CargaItem(
                            Materia = json.optString("Materia"),
                            Grupo = json.optString("Grupo"),
                            Docente = json.optString("Docente"),
                            CreditosMateria = json.optInt("CreditosMateria"),
                            Lunes = json.optString("Lunes"),
                            Martes = json.optString("Martes"),
                            Miercoles = json.optString("Miercoles"),
                            Jueves = json.optString("Jueves"),
                            Viernes = json.optString("Viernes")
                        )
                    }
                    localRepository.saveCarga(items)
                }
                "KARDEX" -> {
                    val array = JSONArray(content)
                    val items = List(array.length()) { i ->
                        val json = array.getJSONObject(i)
                        com.wiz.sice.data.models.KardexItem(
                            clvOficial = json.optString("clvOficial"),
                            materia = json.optString("materia"),
                            periodo = json.optString("periodo"),
                            promedio = json.optString("promedio")
                        )
                    }
                    localRepository.saveKardex(items)
                }
                "CALIF_UNIDADES" -> {
                    val array = JSONArray(content)
                    val items = List(array.length()) { i ->
                        val json = array.getJSONObject(i)
                        val unitsObj = json.optJSONObject("unidades")
                        val unitsMap = mutableMapOf<Int, String>()
                        unitsObj?.keys()?.forEach { key ->
                            unitsMap[key.toInt()] = unitsObj.getString(key)
                        }
                        com.wiz.sice.data.models.CalifUnidadItem(
                            Materia = json.optString("Materia"),
                            Grupo = json.optString("Grupo"),
                            unidades = unitsMap
                        )
                    }
                    localRepository.saveCalifUnidades(items)
                }
                "CALIF_FINALES" -> {
                    val array = JSONArray(content)
                    val items = List(array.length()) { i ->
                        val json = array.getJSONObject(i)
                        com.wiz.sice.data.models.CalifFinalItem(
                            materia = json.optString("materia"),
                            calif = json.optString("calif"),
                            acred = json.optString("acred"),
                            grupo = json.optString("grupo"),
                            Observaciones = json.optString("Observaciones")
                        )
                    }
                    localRepository.saveCalifFinales(items)
                }
            }
            Log.d("GuardarLocal", "Saved $type successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("GuardarLocal", "Error saving $type", e)
            Result.failure()
        }
    }
}
