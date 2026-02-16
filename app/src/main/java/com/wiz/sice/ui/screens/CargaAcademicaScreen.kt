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
import com.wiz.sice.ui.viewModel.SicenetUiState
import com.wiz.sice.ui.viewModel.SicenetViewModel
import org.json.JSONArray
import org.json.JSONObject

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
                is SicenetUiState.DataLoaded -> {
                    if (state.type == "CARGA") {
                        val list = try { JSONArray(state.content) } catch (e: Exception) { null }
                        if (list != null) {
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                items(List(list.length()) { list.getJSONObject(it) }) { item ->
                                    CargaCard(item)
                                }
                            }
                        } else {
                            Text("No se pudieron procesar los datos de la carga académica.", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                is SicenetUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CargaCard(json: JSONObject) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = json.optString("materia", "Sin nombre"),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF062970)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Docente: ${json.optString("docente", "N/A")}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Horario: ${json.optString("horario", "N/A")}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
