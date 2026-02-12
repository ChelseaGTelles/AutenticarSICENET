package com.wiz.sice.data.repository

import com.wiz.sice.data.AccesoLoginRequest
import com.wiz.sice.data.models.LoginResult
import com.wiz.sice.data.models.AlumnoProfile

interface InterfaceRepository {
    suspend fun accesoLogin(request: AccesoLoginRequest): Result<LoginResult>
    suspend fun getAlumno(): Result<AlumnoProfile>
}