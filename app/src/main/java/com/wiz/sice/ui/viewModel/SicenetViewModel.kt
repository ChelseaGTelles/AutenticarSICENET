package com.wiz.sice.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiz.sice.data.repository.SicenetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SicenetUiState {
    object Idle : SicenetUiState()
    object Loading : SicenetUiState()
    data class Success(val message: String) : SicenetUiState()
    data class ProfileLoaded(val profileData: String) : SicenetUiState()
    data class Error(val message: String) : SicenetUiState()
}

class SicenetViewModel(private val repository: SicenetRepository = SicenetRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<SicenetUiState>(SicenetUiState.Idle)
    val uiState: StateFlow<SicenetUiState> = _uiState

    fun login(matricula: String, contrasenia: String, userType: String) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.accesoLogin(matricula, contrasenia, userType).onSuccess { result ->
                if (result.contains("{\"acceso\":true") || result == "1" || result.contains("acceso\":true")) {
                    _uiState.value = SicenetUiState.Success("Login exitoso")
                } else {
                    _uiState.value = SicenetUiState.Error("Credenciales incorrectas: $result")
                }
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading
            repository.getAlumno().onSuccess { result ->
                _uiState.value = SicenetUiState.ProfileLoaded(result)
            }.onFailure {
                _uiState.value = SicenetUiState.Error(it.message ?: "Error al obtener perfil")
            }
        }
    }

    fun resetState() {
        _uiState.value = SicenetUiState.Idle
    }
}
