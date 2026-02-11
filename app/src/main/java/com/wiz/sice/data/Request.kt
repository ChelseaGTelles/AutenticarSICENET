package com.wiz.sice.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

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