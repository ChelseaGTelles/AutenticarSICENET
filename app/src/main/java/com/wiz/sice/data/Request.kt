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

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class CargaEnvelope(
    @field:Element(name = "soap:Body")
    var body: CargaBody? = null
)

class CargaBody(
    @field:Element(name = "getCargaAcademicaByAlumno")
    var method: GetCargaRequest? = null
)

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class KardexEnvelope(
    @field:Element(name = "soap:Body")
    var body: KardexBody? = null
)

class KardexBody(
    @field:Element(name = "getAllKardexConPromedioByAlumno")
    var method: GetKardexRequest? = null
)

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class CalifUnidadesEnvelope(
    @field:Element(name = "soap:Body")
    var body: CalifUnidadesBody? = null
)

class CalifUnidadesBody(
    @field:Element(name = "getCalifUnidadesByAlumno")
    var method: GetCalifUnidadesRequest? = null
)

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class CalifFinalEnvelope(
    @field:Element(name = "soap:Body")
    var body: CalifFinalBody? = null
)

class CalifFinalBody(
    @field:Element(name = "getAllCalifFinalByAlumnos")
    var method: GetCalifFinalRequest? = null
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

@Root(name = "getCargaAcademicaByAlumno")
@Namespace(reference = "http://tempuri.org/")
class GetCargaRequest

@Root(name = "getAllKardexConPromedioByAlumno")
@Namespace(reference = "http://tempuri.org/")
class GetKardexRequest(
    @field:Element(name = "aluLineamiento") var aluLineamiento: Int = 1
)

@Root(name = "getCalifUnidadesByAlumno")
@Namespace(reference = "http://tempuri.org/")
class GetCalifUnidadesRequest

@Root(name = "getAllCalifFinalByAlumnos")
@Namespace(reference = "http://tempuri.org/")
class GetCalifFinalRequest(
    @field:Element(name = "bytModEducativo") var bytModEducativo: Int = 1
)


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

@Root(name = "Envelope", strict = false)
class CargaResponseEnvelope {
    @field:Path("Body/getCargaAcademicaByAlumnoResponse")
    @field:Element(name = "getCargaAcademicaByAlumnoResult", required = false)
    var result: String? = null
}

@Root(name = "Envelope", strict = false)
class KardexResponseEnvelope {
    @field:Path("Body/getAllKardexConPromedioByAlumnoResponse")
    @field:Element(name = "getAllKardexConPromedioByAlumnoResult", required = false)
    var result: String? = null
}

@Root(name = "Envelope", strict = false)
class CalifUnidadesResponseEnvelope {
    @field:Path("Body/getCalifUnidadesByAlumnoResponse")
    @field:Element(name = "getCalifUnidadesByAlumnoResult", required = false)
    var result: String? = null
}

@Root(name = "Envelope", strict = false)
class CalifFinalResponseEnvelope {
    @field:Path("Body/getAllCalifFinalByAlumnosResponse")
    @field:Element(name = "getAllCalifFinalByAlumnosResult", required = false)
    var result: String? = null
}