package com.example.autenticarsicenet.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autenticarsicenet.data.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val profile = mutableStateOf<String?>(null)

    fun getProfile() {
        viewModelScope.launch {
            profile.value = UserRepository.getProfile()
        }
    }
}