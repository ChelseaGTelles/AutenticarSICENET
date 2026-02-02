package com.wiz.sice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    var nombre by mutableStateOf("Usuario BLAH BLAH")
    var numeroControl by mutableStateOf("12345678")
}
