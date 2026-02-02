package com.wiz.sice

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiz.sice.model.LoginRequest
import com.wiz.sice.model.SoapRequestBody
import com.wiz.sice.model.SoapRequestEnvelope
import com.wiz.sice.network.SessionCookieJar
import com.wiz.sice.network.SoapService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.simpleframework.xml.core.Persister
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.StringWriter

class LoginViewModel : ViewModel() {

    var matricula by mutableStateOf("")
    var contrasenia by mutableStateOf("")
    var tipoUsuario by mutableStateOf("ALUMNO")
    var resultText by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)

    private val _navigateToProfile = MutableSharedFlow<Unit>()
    val navigateToProfile = _navigateToProfile.asSharedFlow()

    private val cookieJar = SessionCookieJar()

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://sicenet.surguanajuato.tecnm.mx/")
        .client(client)
        .build()

    private val service = retrofit.create(SoapService::class.java)

    fun login() {
        if (matricula.isBlank() || contrasenia.isBlank()) {
            resultText = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        loginSuccess = false
        resultText = "Iniciando sesión..."

        val login = LoginRequest(
            matricula = matricula,
            contrasenia = contrasenia,
            tipoUsuario = tipoUsuario
        )

        val envelope = SoapRequestEnvelope(
            SoapRequestBody(login)
        )

        val serializer = Persister()
        val writer = StringWriter()
        
        try {
            serializer.write(envelope, writer)
            val xml = writer.toString()
            Log.d("SOAP_XML", xml)

            val body = xml.toRequestBody("text/xml".toMediaType())

            service.login(body).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val cookies = cookieJar.getCookiesAsString()
                    
                    if (cookies.isNotEmpty()) {
                        loginSuccess = true
                        resultText = "LOGIN EXITOSO\nCookie: $cookies"

                        viewModelScope.launch {
                            delay(100)
                            isLoading = false
                            _navigateToProfile.emit(Unit)
                        }
                    } else {
                        isLoading = false
                        loginSuccess = false
                        resultText = "LOGIN INCORRECTO"
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    isLoading = false
                    loginSuccess = false
                    resultText = "ERROR DE CONEXIÓN"
                }
            })
        } catch (e: Exception) {
            isLoading = false
            loginSuccess = false
            resultText = "ERROR"
        }
    }
}
