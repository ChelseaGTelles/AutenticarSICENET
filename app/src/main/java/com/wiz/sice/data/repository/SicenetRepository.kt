package com.wiz.sice.data.repository

import android.util.Log
import com.wiz.sice.data.api.SicenetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    suspend fun accesoLogin(matricula: String, contrasenia: String, userType: String): Result<String> = withContext(Dispatchers.IO) {
        try {

            val preRequest = Request.Builder().url("https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx").build()
            cliente.newCall(preRequest).execute().close()

            val soapRequest = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <accesoLogin xmlns="http://tempuri.org/">
                      <strMatricula>${escapeXml(matricula)}</strMatricula>
                      <strContrasenia>${escapeXml(contrasenia)}</strContrasenia>
                      <tipoUsuario>${escapeXml(userType)}</tipoUsuario>
                    </accesoLogin>
                  </soap:Body>
                </soap:Envelope>
            """.trim()

            val requestBody = soapRequest.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = api.accesoLogin("http://tempuri.org/accesoLogin", requestBody)

            if (response.isSuccessful) {
                val result = response.body()?.result
                if (!result.isNullOrBlank()) {
                    Result.success(result)
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

    suspend fun getAlumno(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val soapRequest = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
                  </soap:Body>
                </soap:Envelope>
            """.trim()

            val requestBody = soapRequest.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = api.getAlumno("http://tempuri.org/getAlumnoAcademicoWithLineamiento", requestBody)

            if (response.isSuccessful) {
                val result = response.body()?.result
                if (!result.isNullOrBlank()) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("No se pudo obtener la información"))
                }
            } else {
                Result.failure(Exception("Error al consultar perfil"))
            }
        } catch (e: Exception) {
            Log.e("SicenetRepo", "Error en perfil", e)
            Result.failure(e)
        }
    }
}
