package com.wiz.sice.data.repository

import android.util.Log
import com.wiz.sice.data.*
import com.wiz.sice.data.api.SicenetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class SicenetRepository {

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

    suspend fun accesoLogin(matricula: String, contrasenia: String, userType: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val preRequest = Request.Builder().url("https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx").build()
            cliente.newCall(preRequest).execute().close()

            val envelope = LoginEnvelope(LoginBody(AccesoLoginRequest(matricula, contrasenia, userType)))
            val response = api.accesoLogin("http://tempuri.org/accesoLogin", envelope)

            if (response.isSuccessful) {
                val result = response.body()?.result
                if (!result.isNullOrBlank()) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Credenciales incorrectas o respuesta vacía"))
                }
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SicenetRepo", "Error en login", e)
            Result.failure(e)
        }
    }

    suspend fun getAlumno(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val envelope = PerfilEnvelope(PerfilBody(GetAlumnoRequest()))
            val response = api.getAlumno("http://tempuri.org/getAlumnoAcademicoWithLineamiento", envelope)

            if (response.isSuccessful) {
                val result = response.body()?.result
                if (!result.isNullOrBlank()) {
                    Result.success(result)
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