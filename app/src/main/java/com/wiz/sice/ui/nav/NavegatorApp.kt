package com.wiz.sice.ui.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wiz.sice.ui.screens.*
import com.wiz.sice.ui.viewModel.SicenetViewModel

@Composable
fun SicenetApp() {
    val navController = rememberNavController()
    val viewModel: SicenetViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel, onLoginSuccess = {
                navController.navigate("profile") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("profile") {
            PerfilScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }
        composable("calificaciones_unidad") { 
            CalificacionesPorUnidadScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) 
        }
        composable("calificaciones_finales") { 
            CalificacionesFinalesScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) 
        }
        composable("kardex") { 
            KardexScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) 
        }
        composable("carga_academica") { 
            CargaAcademicaScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}