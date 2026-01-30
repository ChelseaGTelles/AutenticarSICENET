package com.example.autenticarsicenet.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autenticarsicenet.ui.login.LoginScreen
import com.example.autenticarsicenet.ui.profile.ProfileScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("profile") { ProfileScreen() }
    }
}
