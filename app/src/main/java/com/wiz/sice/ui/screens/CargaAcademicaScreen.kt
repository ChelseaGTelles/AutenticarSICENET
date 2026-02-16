package com.wiz.sice.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiz.sice.data.models.CargaItem
import com.wiz.sice.ui.viewModel.SicenetUiState
import com.wiz.sice.ui.viewModel.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaAcademicaScreen(viewModel: SicenetViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCarga()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carga Académica", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF062970))
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is SicenetUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SicenetUiState.CargaLoaded -> {
                    if (state.items.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(state.items) { item ->
                                CargaCard(item)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron materias cargadas.", color = Color.Gray)
                        }
                    }
                }
                is SicenetUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CargaCard(item: CargaItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.materia,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF062970)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Docente: ${item.docente}", fontSize = 14.sp)
            Text(text = "Horario: ${item.horario}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "Aula: ${item.aula}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
