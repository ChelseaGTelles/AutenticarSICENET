package com.wiz.sice.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class LoginEnvelope(
    @field:Element(name = "soap:Body")
    var body: LoginBody? = null
)

class LoginBody(
    @field:Element(name = "accesoLogin")
    var method: AccesoLoginRequest? = null
)



@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class PerfilEnvelope(
    @field:Element(name = "soap:Body")
    var body: PerfilBody? = null
)

class PerfilBody(
    @field:Element(name = "getAlumnoAcademicoWithLineamiento")
    var method: GetAlumnoRequest? = null
)

@Root(name = "accesoLogin")
@Namespace(reference = "http://tempuri.org/")
class AccesoLoginRequest(
    @field:Element(name = "strMatricula") var strMatricula: String = "",
    @field:Element(name = "strContrasenia") var strContrasenia: String = "",
    @field:Element(name = "tipoUsuario") var tipoUsuario: String = ""
)

@Root(name = "getAlumnoAcademicoWithLineamiento")
@Namespace(reference = "http://tempuri.org/")
class GetAlumnoRequest

@Root(name = "Envelope", strict = false)
class LoginResponseEnvelope {
    @field:Path("Body/accesoLoginResponse")
    @field:Element(name = "accesoLoginResult", required = false)
    var result: String? = null
}

@Root(name = "Envelope", strict = false)
class AlumnoResponseEnvelope {
    @field:Path("Body/getAlumnoAcademicoWithLineamientoResponse")
    @field:Element(name = "getAlumnoAcademicoWithLineamientoResult", required = false)
    var result: String? = null
}