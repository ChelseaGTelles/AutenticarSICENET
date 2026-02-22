package com.wiz.sice.ui.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.wiz.sice.data.AccesoLoginRequest
import com.wiz.sice.data.local.SicenetDatabase
import com.wiz.sice.data.models.*
import com.wiz.sice.data.repository.InterfaceRepository
import com.wiz.sice.data.repository.LocalRepository
import com.wiz.sice.data.repository.LocalRepositoryInterface
import com.wiz.sice.data.repository.SicenetRepository
import com.wiz.sice.data.workers.Sincronizar
import com.wiz.sice.data.workers.GuardarLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class SicenetUiState {
    object Idle : SicenetUiState()
    object Loading : SicenetUiState()
    data class Success(val loginResult: LoginResult) : SicenetUiState()
    data class ProfileLoaded(val profile: AlumnoProfile, val lastUpdated: String? = null, val fromCache: Boolean = false) : SicenetUiState()
    data class CargaLoaded(val items: List<CargaItem>, val lastUpdated: String? = null, val fromCache: Boolean = false) : SicenetUiState()
    data class KardexLoaded(val items: List<KardexItem>, val lastUpdated: String? = null, val fromCache: Boolean = false) : SicenetUiState()
    data class UnidadesLoaded(val items: List<CalifUnidadItem>, val lastUpdated: String? = null, val fromCache: Boolean = false) : SicenetUiState()
    data class FinalesLoaded(val items: List<CalifFinalItem>, val lastUpdated: String? = null, val fromCache: Boolean = false) : SicenetUiState()
    data class Error(val message: String) : SicenetUiState()
}

class SicenetViewModel(
    application: Application,
    private val repository: InterfaceRepository = SicenetRepository(),
    private val localRepository: LocalRepositoryInterface = LocalRepository(SicenetDatabase.getDatabase(application))
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<SicenetUiState>(SicenetUiState.Idle)
    val uiState: StateFlow<SicenetUiState> = _uiState

    private val workManager = WorkManager.getInstance(application)

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp == 0L) return ""
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun login(matricula: String, contrasenia: String, userType: String) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading

            if (isNetworkAvailable()) {
                val request = AccesoLoginRequest(matricula, contrasenia, userType)
                repository.accesoLogin(request).onSuccess { result ->
                    if (result.acceso) {
                        localRepository.saveSession(matricula, contrasenia, userType)
                        _uiState.value = SicenetUiState.Success(result)
                        startProfileSync()
                    } else {
                        _uiState.value = SicenetUiState.Error("Credenciales incorrectas: ${result.mensaje}")
                    }
                }.onFailure {
                    _uiState.value = SicenetUiState.Error(it.message ?: "Error desconocido")
                }
            } else {
                val session = localRepository.getSession()
                if (session != null && session.matricula == matricula) {
                    _uiState.value = SicenetUiState.Success(LoginResult(acceso = true, mensaje = "Sesión local"))
                } else {
                    _uiState.value = SicenetUiState.Error("Sin conexión a internet y sin sesión guardada")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            localRepository.clearAll()
            _uiState.value = SicenetUiState.Idle
        }
    }

    private fun startProfileSync() {
        val fetchWork = OneTimeWorkRequestBuilder<Sincronizar>()
            .setInputData(workDataOf(Sincronizar.KEY_TYPE to "PROFILE"))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        val saveWork = OneTimeWorkRequestBuilder<GuardarLocal>()
            .build()

        workManager.beginUniqueWork(
            "profile_sync",
            ExistingWorkPolicy.REPLACE,
            fetchWork
        ).then(saveWork).enqueue()

        workManager.getWorkInfoByIdLiveData(fetchWork.id).observeForever { workInfo ->
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                viewModelScope.launch {
                    getProfile()
                }
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading

            if (isNetworkAvailable()) {
                repository.getAlumno().onSuccess { profile ->
                    localRepository.saveProfile(profile)
                    val timestamp = formatTimestamp(System.currentTimeMillis())
                    _uiState.value = SicenetUiState.ProfileLoaded(profile, timestamp, false)
                }.onFailure {
                    loadProfileFromLocal()
                }
            } else {
                loadProfileFromLocal()
            }
        }
    }

    private suspend fun loadProfileFromLocal() {
        val profile = localRepository.getProfile()
        if (profile != null) {
            val timestamp = formatTimestamp(localRepository.getProfileLastUpdated())
            _uiState.value = SicenetUiState.ProfileLoaded(profile, timestamp, true)
        } else {
            _uiState.value = SicenetUiState.Error("No hay datos guardados localmente")
        }
    }

    fun getCarga() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading

            if (isNetworkAvailable()) {
                repository.getCargaAcademicaByAlumno().onSuccess { items ->
                    localRepository.saveCarga(items)
                    val timestamp = formatTimestamp(System.currentTimeMillis())
                    _uiState.value = SicenetUiState.CargaLoaded(items, timestamp, false)
                }.onFailure {
                    loadCargaFromLocal()
                }
            } else {
                loadCargaFromLocal()
            }
        }
    }

    private suspend fun loadCargaFromLocal() {
        val items = localRepository.getCarga()
        if (items != null) {
            val timestamp = formatTimestamp(localRepository.getDataLastUpdated("CARGA"))
            _uiState.value = SicenetUiState.CargaLoaded(items, timestamp, true)
        } else {
            _uiState.value = SicenetUiState.Error("No hay datos guardados localmente")
        }
    }

    fun getKardex(lineamiento: Int) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading

            if (isNetworkAvailable()) {
                repository.getAllKardexConPromedioByAlumno(lineamiento).onSuccess { items ->
                    localRepository.saveKardex(items)
                    val timestamp = formatTimestamp(System.currentTimeMillis())
                    _uiState.value = SicenetUiState.KardexLoaded(items, timestamp, false)
                }.onFailure {
                    loadKardexFromLocal()
                }
            } else {
                loadKardexFromLocal()
            }
        }
    }

    private suspend fun loadKardexFromLocal() {
        val items = localRepository.getKardex()
        if (items != null) {
            val timestamp = formatTimestamp(localRepository.getDataLastUpdated("KARDEX"))
            _uiState.value = SicenetUiState.KardexLoaded(items, timestamp, true)
        } else {
            _uiState.value = SicenetUiState.Error("No hay datos guardados localmente")
        }
    }

    fun getCalifUnidades() {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading

            if (isNetworkAvailable()) {
                repository.getCalifUnidadesByAlumno().onSuccess { items ->
                    localRepository.saveCalifUnidades(items)
                    val timestamp = formatTimestamp(System.currentTimeMillis())
                    _uiState.value = SicenetUiState.UnidadesLoaded(items, timestamp, false)
                }.onFailure {
                    loadCalifUnidadesFromLocal()
                }
            } else {
                loadCalifUnidadesFromLocal()
            }
        }
    }

    private suspend fun loadCalifUnidadesFromLocal() {
        val items = localRepository.getCalifUnidades()
        if (items != null) {
            val timestamp = formatTimestamp(localRepository.getDataLastUpdated("CALIF_UNIDADES"))
            _uiState.value = SicenetUiState.UnidadesLoaded(items, timestamp, true)
        } else {
            _uiState.value = SicenetUiState.Error("No hay datos guardados localmente")
        }
    }

    fun getCalifFinales(modEducativo: Int) {
        viewModelScope.launch {
            _uiState.value = SicenetUiState.Loading

            if (isNetworkAvailable()) {
                repository.getAllCalifFinalByAlumnos(modEducativo).onSuccess { items ->
                    localRepository.saveCalifFinales(items)
                    val timestamp = formatTimestamp(System.currentTimeMillis())
                    _uiState.value = SicenetUiState.FinalesLoaded(items, timestamp, false)
                }.onFailure {
                    loadCalifFinalesFromLocal()
                }
            } else {
                loadCalifFinalesFromLocal()
            }
        }
    }

    private suspend fun loadCalifFinalesFromLocal() {
        val items = localRepository.getCalifFinales()
        if (items != null) {
            val timestamp = formatTimestamp(localRepository.getDataLastUpdated("CALIF_FINALES"))
            _uiState.value = SicenetUiState.FinalesLoaded(items, timestamp, true)
        } else {
            _uiState.value = SicenetUiState.Error("No hay datos guardados localmente")
        }
    }
}

class SicenetViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SicenetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SicenetViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
