package com.wiz.sice.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiz.sice.data.models.AlumnoProfile
import com.wiz.sice.ui.components.SicenetBottomBar
import com.wiz.sice.ui.viewModel.SicenetUiState
import com.wiz.sice.ui.viewModel.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(viewModel: SicenetViewModel, onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState !is SicenetUiState.ProfileLoaded) {
            viewModel.getProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF062970))
            )
        },
        bottomBar = {
            SicenetBottomBar(currentRoute = "profile", onNavigate = onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when (val state = uiState) {
                    is SicenetUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF062970))
                        }
                    }
                    is SicenetUiState.ProfileLoaded -> {
                        if (state.fromCache && !state.lastUpdated.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                            ) {
                                Text(
                                    text = "Datos guardados, última actualización: ${state.lastUpdated}",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 12.sp,
                                    color = Color(0xFF827717)
                                )
                            }
                        }
                        ProfileDataDisplay(state.profile)
                    }
                    is SicenetUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.getProfile() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF062970))
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun ProfileDataDisplay(profile: AlumnoProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información Académica",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF062970),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ProfileItem("Matrícula", profile.matricula)
            ProfileItem("Nombre", profile.nombre)
            ProfileItem("Carrera", profile.carrera)
            ProfileItem("Especialidad", profile.especialidad)
            ProfileItem("Semestre Actual", profile.semActual)
            ProfileItem("Créditos Acumulados", profile.cdtosAcumulados)
            ProfileItem("Créditos Actuales", profile.cdtosActuales)
            ProfileItem("Fecha Reinscripción", profile.fechaReins)
            ProfileItem("¿Tiene Adeudo?", if (profile.adeudo) "Sí" else "No")
            ProfileItem("¿Está Inscrito?", if (profile.inscrito) "Sí" else "No")
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(0.55f)
        )
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
}
