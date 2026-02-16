package com.wiz.sice.data.repository

import android.util.Log
import com.wiz.sice.data.*
import com.wiz.sice.data.api.SicenetApi
import com.wiz.sice.data.models.AlumnoProfile
import com.wiz.sice.data.models.LoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class SicenetRepository : InterfaceRepository {

    private val cliente: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            private val cookieMap = mutableMapOf<String, MutableMap<String, Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val hostCookies = cookieMap.getOrPut(url.host) { mutableMapOf() }
                cookies.forEach { cookie ->
                    hostCookies[cookie.name] = cookie
                }
                Log.d("SicenetRepo", "Cookies actualizadas para ${url.host}: ${hostCookies.size} cookies activas")
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookieMap[url.host]?.values?.toList() ?: listOf()
                Log.d("SicenetRepo", "Cargando ${cookies.size} cookies para ${url.host}")
                return cookies
            }
        })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sicenet.surguanajuato.tecnm.mx/")
        .client(cliente)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    private val api = retrofit.create(SicenetApi::class.java)

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
                val resultString = response.body()?.result
                if (!resultString.isNullOrBlank()) {
                    val isSuccess = resultString.contains("\"acceso\":true") || resultString == "1"
                    Result.success(LoginResult(acceso = isSuccess, mensaje = if (isSuccess) "Acceso concedido" else "Acceso denegado", rawResponse = resultString))
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}"))
            }
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
                val resultString = response.body()?.result
                if (!resultString.isNullOrBlank()) {
                    try {
                        val json = JSONObject(resultString)
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
                            rawJson = resultString
                        )
                        Result.success(profile)
                    } catch (e: Exception) {
                        Result.success(AlumnoProfile(rawJson = resultString))
                    }
                } else {
                    Result.failure(Exception("No se pudo obtener la información del alumno"))
                }
            } else {
                Result.failure(Exception("Error al consultar perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SicenetRepo", "Error en perfil", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllCalifFinalByAlumnos(modEducativo: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = CalifFinalEnvelope(CalifFinalBody(GetCalifFinalRequest(modEducativo)))
            val response = api.getCalifFinal("http://tempuri.org/getAllCalifFinalByAlumnos", envelope)
            if (response.isSuccessful) {
                Result.success(response.body()?.result ?: "")
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCalifUnidadesByAlumno(): Result<String> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = CalifUnidadesEnvelope(CalifUnidadesBody(GetCalifUnidadesRequest()))
            val response = api.getCalifUnidades("http://tempuri.org/getCalifUnidadesByAlumno", envelope)
            if (response.isSuccessful) {
                Result.success(response.body()?.result ?: "")
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllKardexConPromedioByAlumno(aluLineamiento: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = KardexEnvelope(KardexBody(GetKardexRequest(aluLineamiento)))
            val response = api.getKardex("http://tempuri.org/getAllKardexConPromedioByAlumno", envelope)
            if (response.isSuccessful) {
                Result.success(response.body()?.result ?: "")
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCargaAcademicaByAlumno(): Result<String> = withContext(Dispatchers.IO) {
        try {
            keepAlive()
            val envelope = CargaEnvelope(CargaBody(GetCargaRequest()))
            val response = api.getCarga("http://tempuri.org/getCargaAcademicaByAlumno", envelope)
            if (response.isSuccessful) {
                Result.success(response.body()?.result ?: "")
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}