package com.example.autenticarsicenet.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autenticarsicenet.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val loginSuccess = mutableStateOf(false)

    fun login(matricula: String, contrasenia: String) {
        viewModelScope.launch {
            loginSuccess.value = UserRepository.login(matricula, contrasenia)
        }
    }
}