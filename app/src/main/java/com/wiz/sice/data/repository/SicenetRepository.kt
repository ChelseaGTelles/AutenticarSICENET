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
            private val cookie = mutableMapOf<String, List<Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                Log.d("SicenetRepo", "Guardando cookies: $cookies")
                cookie[url.host] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookie[url.host] ?: listOf()
                Log.d("SicenetRepo", "Cargando cookies para ${url.host}: $cookies")
                return cookies
            }
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sicenet.surguanajuato.tecnm.mx/")
        .client(cliente)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    private val api = retrofit.create(SicenetApi::class.java)

    override suspend fun accesoLogin(request: AccesoLoginRequest): Result<LoginResult> = withContext(Dispatchers.IO) {
        try {
            val preRequest = Request.Builder().url("https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx").build()
            cliente.newCall(preRequest).execute().close()

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
}
