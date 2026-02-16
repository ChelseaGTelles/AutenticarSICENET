package com.wiz.sice.data.repository

import android.util.Log
import com.wiz.sice.data.*
import com.wiz.sice.data.api.SicenetApi
import com.wiz.sice.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class SicenetRepository : InterfaceRepository {

    private val cookieMap = mutableMapOf<String, MutableMap<String, Cookie>>()

    private val cliente: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val hostCookies = cookieMap.getOrPut(url.host) { mutableMapOf() }
                cookies.forEach { cookie -> hostCookies[cookie.name] = cookie }
                Log.d("SicenetRepo", "Cookies guardadas para ${url.host}: ${cookies.map { it.name }}")
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookieMap[url.host]?.values?.toList() ?: listOf()
                Log.d("SicenetRepo", "Enviando ${cookies.size} cookies a ${url.host}")
                return cookies
            }
        })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sicenet.surguanajuato.tecnm.mx/")
        .client(cliente)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    private val api = retrofit.create(SicenetApi::class.java)

    override fun logout() {
        cookieMap.clear()
        Log.d("SicenetRepo", "Sesión cerrada: cookies eliminadas")
    }

    private suspend fun keepAlive() {
        try {
            val request = Request.Builder().url("https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx").build()
            cliente.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e("SicenetRepo", "Error en KeepAlive", e)
        }
    }

    override suspend fun accesoLogin(request: AccesoLoginRequest): Result<LoginResult> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = LoginEnvelope(LoginBody(request))
            val response = api.accesoLogin("http://tempuri.org/accesoLogin", envelope)

            if (response.isSuccessful) {
                val res = response.body()?.result
                Log.d("SicenetRepo", "Login Result: $res")
                if (!res.isNullOrBlank()) {
                    val isSuccess = res.contains("\"acceso\":true") || res == "1"
                    Result.success(LoginResult(acceso = isSuccess, mensaje = if (isSuccess) "OK" else "Denegado", rawResponse = res))
                } else Result.failure(Exception("Respuesta de login vacía"))
            } else Result.failure(Exception("HTTP ${response.code()} en Login"))
        } catch (e: Exception) {
            Log.e("SicenetRepo", "Error en login", e)
            Result.failure(e)
        }
    }

    override suspend fun getAlumno(): Result<AlumnoProfile> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = PerfilEnvelope(PerfilBody(GetAlumnoRequest()))
            val response = api.getAlumno("http://tempuri.org/getAlumnoAcademicoWithLineamiento", envelope)

            if (response.isSuccessful) {
                val res = response.body()?.result
                Log.d("SicenetRepo", "Perfil Result: $res")
                if (!res.isNullOrBlank()) {
                    val json = JSONObject(res)
                    val profile = AlumnoProfile(
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
                        rawJson = res
                    )
                    Result.success(profile)
                } else Result.failure(Exception("Perfil vacío"))
            } else Result.failure(Exception("HTTP ${response.code()} en Perfil"))
        } catch (e: Exception) {
            Log.e("SicenetRepo", "Error en perfil", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllCalifFinalByAlumnos(modEducativo: Int): Result<List<CalifFinalItem>> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = CalifFinalEnvelope(CalifFinalBody(GetCalifFinalRequest(modEducativo)))
            val response = api.getCalifFinal("http://tempuri.org/getAllCalifFinalByAlumnos", envelope)
            val res = response.body()?.result
            if (response.isSuccessful && res != null) {
                val list = smartParse(res).map { json ->
                    CalifFinalItem(
                        materia = json.optString("materia", json.optString("Materia", "N/A")),
                        calificacionFinal = json.optString("calificacionFinal", json.optString("Promedio", "N/A"))
                    )
                }
                Result.success(list)
            } else Result.failure(Exception("Error en CalifFinales"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getCalifUnidadesByAlumno(): Result<List<CalifUnidadItem>> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = CalifUnidadesEnvelope(CalifUnidadesBody(GetCalifUnidadesRequest()))
            val response = api.getCalifUnidades("http://tempuri.org/getCalifUnidadesByAlumno", envelope)
            val res = response.body()?.result
            if (response.isSuccessful && res != null) {
                val list = smartParse(res).map { json ->
                    val units = mutableMapOf<Int, String>()
                    for (i in 1..10) {
                        val valUnit = json.optString("C$i", "")
                        if (valUnit.isNotEmpty() && valUnit != "null") {
                            units[i] = valUnit
                        }
                    }
                    CalifUnidadItem(
                        materia = json.optString("materia", json.optString("Materia", "N/A")),
                        unidades = units
                    )
                }
                Result.success(list)
            } else Result.failure(Exception("Error en CalifUnidades"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getAllKardexConPromedioByAlumno(aluLineamiento: Int): Result<List<KardexItem>> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = KardexEnvelope(KardexBody(GetKardexRequest(aluLineamiento)))
            val response = api.getKardex("http://tempuri.org/getAllKardexConPromedioByAlumno", envelope)
            val res = response.body()?.result
            if (response.isSuccessful && res != null) {
                val list = smartParse(res).map { json ->
                    KardexItem(
                        clvOficial = json.optString("clvOficial", json.optString("ClvOficial", "N/A")),
                        materia = json.optString("materia", json.optString("Materia", "N/A")),
                        periodo = json.optString("periodo", json.optString("Periodo", "N/A")),
                        promedio = json.optString("promedio", json.optString("Promedio", "N/A"))
                    )
                }
                Result.success(list)
            } else Result.failure(Exception("Error en Kardex"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getCargaAcademicaByAlumno(): Result<List<CargaItem>> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = CargaEnvelope(CargaBody(GetCargaRequest()))
            val response = api.getCarga("http://tempuri.org/getCargaAcademicaByAlumno", envelope)
            val res = response.body()?.result
            if (response.isSuccessful && res != null) {
                val list = smartParse(res).map { json ->
                    CargaItem(
                        materia = json.optString("materia", json.optString("Materia", "N/A")),
                        docente = json.optString("docente", json.optString("Docente", "N/A")),
                        horario = json.optString("horario", json.optString("Horario", "N/A")),
                        aula = json.optString("aula", json.optString("Aula", "N/A"))
                    )
                }
                Result.success(list)
            } else Result.failure(Exception("Error en Carga"))
        } catch (e: Exception) { Result.failure(e) }
    }

    private fun smartParse(jsonStr: String): List<JSONObject> {
        val results = mutableListOf<JSONObject>()
        try {
            val trimmed = jsonStr.trim()
            if (trimmed.startsWith("[")) {
                val array = JSONArray(trimmed)
                for (i in 0 until array.length()) results.add(array.getJSONObject(i))
            } else if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                val d = obj.optString("d", "")
                if (d.startsWith("[")) return smartParse(d)
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val potentialArray = obj.optJSONArray(key)
                    if (potentialArray != null) {
                        for (i in 0 until potentialArray.length()) results.add(potentialArray.getJSONObject(i))
                        return results
                    }
                    val potentialString = obj.optString(key, "")
                    if (potentialString.startsWith("[")) return smartParse(potentialString)
                }
            }
        } catch (e: Exception) { }
        return results
    }
}
