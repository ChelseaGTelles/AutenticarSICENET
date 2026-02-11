package com.wiz.sice.data.api

import com.wiz.sice.data.AlumnoResponseEnvelope
import com.wiz.sice.data.LoginResponseEnvelope
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SicenetApi {
    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("ws/wsalumnos.asmx")
    suspend fun accesoLogin(
        @Header("SOAPAction") soapAction: String,
        @Body body: RequestBody
    ): Response<LoginResponseEnvelope>

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("ws/wsalumnos.asmx")
    suspend fun getAlumno(
        @Header("SOAPAction") soapAction: String,
        @Body body: RequestBody
    ): Response<AlumnoResponseEnvelope>
}