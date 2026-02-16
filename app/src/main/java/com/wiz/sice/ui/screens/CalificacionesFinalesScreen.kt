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
fun CalificacionesFinalesScreen(viewModel: SicenetViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCalifFinales(2) // Asumiendo un valor por defecto para modEducativo
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones Finales", color = Color.White) },
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
                    if (state.type == "FINALES") {
                        val list = try { JSONArray(state.content) } catch (e: Exception) { null }
                        if (list != null) {
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                items(List(list.length()) { list.getJSONObject(it) }) { item ->
                                    FinalCard(item)
                                }
                            }
                        } else {
                            Text("No se pudieron procesar los datos.")
                        }
                    }
                }
                is SicenetUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }
        }
    }
}

@Composable
fun FinalCard(json: JSONObject) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = json.optString("materia"), fontWeight = FontWeight.Bold)
            Text(text = "Calificación Final: ${json.optString("calificacionFinal")}")
        }
    }
}