package com.wiz.sice.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiz.sice.data.AccesoLoginRequest
import com.wiz.sice.data.models.*
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
    data class CargaLoaded(val items: List<CargaItem>) : SicenetUiState()
    data class KardexLoaded(val items: List<KardexItem>) : SicenetUiState()
    data class UnidadesLoaded(val items: List<CalifUnidadItem>) : SicenetUiState()
    data class FinalesLoaded(val items: List<CalifFinalItem>) : SicenetUiState()
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

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = SicenetUiState.Idle
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
            repository.getCargaAcademicaByAlumno().onSuccess { items ->
                _uiState.value = SicenetUiState.CargaLoaded(items)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener carga académica")
            }
        }
    }

    fun getKardex(lineamiento: Int) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getAllKardexConPromedioByAlumno(lineamiento).onSuccess { items ->
                _uiState.value = SicenetUiState.KardexLoaded(items)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener kardex")
            }
        }
    }

    fun getCalifUnidades() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getCalifUnidadesByAlumno().onSuccess { items ->
                _uiState.value = SicenetUiState.UnidadesLoaded(items)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener calificaciones por unidad")
            }
        }
    }

    fun getCalifFinales(modEducativo: Int) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getAllCalifFinalByAlumnos(modEducativo).onSuccess { items ->
                _uiState.value = SicenetUiState.FinalesLoaded(items)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener calificaciones finales")
            }
        }
    }
}
