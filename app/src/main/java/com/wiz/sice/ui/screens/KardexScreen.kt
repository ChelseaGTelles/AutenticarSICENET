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
import com.wiz.sice.data.models.KardexItem
import com.wiz.sice.ui.viewModel.SicenetUiState
import com.wiz.sice.ui.viewModel.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(viewModel: SicenetViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getKardex(3)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cardex Académico", color = Color.White) },
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
                is SicenetUiState.KardexLoaded -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (state.fromCache && !state.lastUpdated.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
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
                        if (state.items.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.items) { item ->
                                    KardexCard(item)
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron registros en el Kardex.", color = Color.Gray)
                            }
                        }
                    }
                }
                is SicenetUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun KardexCard(item: KardexItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = item.clvOficial,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Semestre " + item.periodo,
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.materia,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF062970)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(text = "Calificación: ", fontSize = 14.sp)
                Text(
                    text = item.promedio,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if ((item.promedio.toIntOrNull() ?: 0) >= 70) Color.Black else Color.Red
                )
            }
        }
    }
}
