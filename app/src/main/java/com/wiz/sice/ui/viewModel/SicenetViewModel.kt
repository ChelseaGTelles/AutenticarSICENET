package com.wiz.sice.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiz.sice.data.AccesoLoginRequest
import com.wiz.sice.data.models.AlumnoProfile
import com.wiz.sice.data.models.LoginResult
import com.wiz.sice.data.repository.InterfaceRepository
import com.wiz.sice.data.repository.SicenetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SicenetUiState {
    object Idle : SicenetUiState()
    object Loading : SicenetUiState()
    data class Success(val loginResult: LoginResult) : SicenetUiState()
    data class ProfileLoaded(val profile: AlumnoProfile) : SicenetUiState()
    data class DataLoaded(val type: String, val content: String) : SicenetUiState()
    data class Error(val message: String) : SicenetUiState()
}

class SicenetViewModel(private val repository: InterfaceRepository = SicenetRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<SicenetUiState>(SicenetUiState.Idle)
    val uiState: StateFlow<SicenetUiState> = _uiState

    fun login(matricula: String, contrasenia: String, userType: String) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            val request = AccesoLoginRequest(matricula, contrasenia, userType)
            repository.accesoLogin(request).onSuccess { result ->
                if (result.acceso) {
                    _uiState.value = SicenetUiState.Success(result)
                } else {
                    _uiState.value = SicenetUiState.Error("Credenciales incorrectas: ${result.mensaje}")
                }
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getAlumno().onSuccess { profile ->
                _uiState.value = SicenetUiState.ProfileLoaded(profile)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener perfil")
            }
        }
    }

    fun getCarga() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getCargaAcademicaByAlumno().onSuccess { result ->
                _uiState.value = SicenetUiState.DataLoaded("CARGA", result)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener carga académica")
            }
        }
    }

    fun getKardex(lineamiento: Int) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getAllKardexConPromedioByAlumno(lineamiento).onSuccess { result ->
                _uiState.value = SicenetUiState.DataLoaded("KARDEX", result)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener kardex")
            }
        }
    }

    fun getCalifUnidades() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getCalifUnidadesByAlumno().onSuccess { result ->
                _uiState.value = SicenetUiState.DataLoaded("UNIDADES", result)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener calificaciones por unidad")
            }
        }
    }

    fun getCalifFinales(modEducativo: Int) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getAllCalifFinalByAlumnos(modEducativo).onSuccess { result ->
                _uiState.value = SicenetUiState.DataLoaded("FINALES", result)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener calificaciones finales")
            }
        }
    }
}
