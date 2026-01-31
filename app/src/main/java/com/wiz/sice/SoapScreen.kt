package com.wiz.sice

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wiz.sice.model.LoginRequest
import com.wiz.sice.model.SoapRequestBody
import com.wiz.sice.model.SoapRequestEnvelope
import com.wiz.sice.network.SessionCookieJar
import com.wiz.sice.network.SoapService
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

@Composable
fun SoapScreen() {

    var estado by remember { mutableStateOf("Esperando acción") }

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = { enviarSoap { estado = it } }
            ) {
                Text("Enviar SOAP")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = estado)
        }
    }
}

fun enviarSoap(onResultado: (String) -> Unit) {

    val login = LoginRequest(
        matricula = "S22120249",
        contrasenia = "x_5N%A",
        tipoUsuario = "ALUMNO"
    )

    val envelope = SoapRequestEnvelope(
        SoapRequestBody(login)
    )

    // Serializar XML
    val serializer = Persister()
    val writer = StringWriter()
    serializer.write(envelope, writer)

    val xml = writer.toString()
    Log.d("SOAP_XML", xml)

    val retrofit = Retrofit.Builder()
        .baseUrl("http://sicenet.surguanajuato.tecnm.mx/")
        .build()

    val service = retrofit.create(SoapService::class.java)

    val body = xml.toRequestBody("text/xml".toMediaType())

    val cookieJar = SessionCookieJar()

    val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    service.login(body).enqueue(object : Callback<ResponseBody> {

        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
            val headers = response.headers()
            val cookies = headers.values("Set-Cookie")

            onResultado(
                """
        HTTP ${response.code()}
        SET-COOKIE:
        ${if (cookies.isEmpty()) "NO HAY COOKIE" else cookies.joinToString("\n")}
        """.trimIndent()
            )
        }


        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            onResultado("Error: ${t.message}")
        }
    })
}