package com.wiz.sice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiz.sice.data.models.AlumnoProfile
import com.wiz.sice.ui.viewModel.SicenetUiState
import com.wiz.sice.ui.viewModel.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(viewModel: SicenetViewModel, onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    // Corregido: Llamar a getProfile si no hemos cargado el perfil aún
    LaunchedEffect(Unit) {
        if (uiState !is SicenetUiState.ProfileLoaded) {
            viewModel.getProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                    
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú", tint = Color.White)
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Calificaciones Por Unidad") },
                            onClick = {
                                showMenu = false
                                onNavigate("calificaciones_unidad")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Calificaciones Finales") },
                            onClick = {
                                showMenu = false
                                onNavigate("calificaciones_finales")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Kardex") },
                            onClick = {
                                showMenu = false
                                onNavigate("kardex")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Carga Académica") },
                            onClick = {
                                showMenu = false
                                onNavigate("carga_academica")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF062970))
            )
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
                            CircularProgressIndicator()
                        }
                    }
                    is SicenetUiState.ProfileLoaded -> {
                        ProfileDataDisplay(state.profile)
                    }
                    is SicenetUiState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.getProfile() }) {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileItem("FechaReins", profile.fechaReins)
            ProfileItem("ModEducativo", profile.modEducativo)
            ProfileItem("Adeudo", profile.adeudo.toString())
            ProfileItem("UrlFoto", profile.urlFoto)
            ProfileItem("AdeudoDescripcion", profile.adeudoDescripcion)
            ProfileItem("Inscrito", profile.inscrito.toString())
            ProfileItem("Estatus", profile.estatus)
            ProfileItem("SemActual", profile.semActual)
            ProfileItem("CdtosAcumulados", profile.cdtosAcumulados)
            ProfileItem("CdtosActuales", profile.cdtosActuales)
            ProfileItem("Especialidad", profile.especialidad)
            ProfileItem("Carrera", profile.carrera)
            ProfileItem("Lineamiento", profile.lineamiento)
            ProfileItem("Nombre", profile.nombre)
            ProfileItem("Matricula", profile.matricula)
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.weight(0.6f)
        )
    }
    HorizontalDivider()
}
