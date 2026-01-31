package com.wiz.sice.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "Envelope", strict = false)
@Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
data class SoapRequestEnvelope(

    @field:Element(name = "Body", required = false)
    @field:Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    var body: SoapRequestBody? = null
)

@Root(name = "Body", strict = false)
@Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
data class SoapRequestBody(

    @field:Element(name = "accesoLogin", required = false)
    @field:Namespace(reference = "http://tempuri.org/")
    var login: LoginRequest? = null
)

@Root(name = "accesoLogin", strict = false)
@Namespace(reference = "http://tempuri.org/")
data class LoginRequest(

    @field:Element(name = "strMatricula", required = false)
    var matricula: String? = null,

    @field:Element(name = "strContrasenia", required = false)
    var contrasenia: String? = null,

    @field:Element(name = "tipoUsuario", required = false)
    var tipoUsuario: String? = null
)
