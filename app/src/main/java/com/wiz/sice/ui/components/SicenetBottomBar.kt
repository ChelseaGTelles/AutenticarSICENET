package com.wiz.sice.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SicenetBottomBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) },
            selected = currentRoute == "profile",
            onClick = { if (currentRoute != "profile") onNavigate("profile") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Unidades") },
            label = { Text("Unidades", fontSize = 10.sp) },
            selected = currentRoute == "calificaciones_unidad",
            onClick = { if (currentRoute != "calificaciones_unidad") onNavigate("calificaciones_unidad") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Finales") },
            label = { Text("Finales", fontSize = 10.sp) },
            selected = currentRoute == "calificaciones_finales",
            onClick = { if (currentRoute != "calificaciones_finales") onNavigate("calificaciones_finales") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Kardex") },
            label = { Text("Kardex", fontSize = 10.sp) },
            selected = currentRoute == "kardex",
            onClick = { if (currentRoute != "kardex") onNavigate("kardex") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Carga") },
            label = { Text("Carga", fontSize = 10.sp) },
            selected = currentRoute == "carga_academica",
            onClick = { if (currentRoute != "carga_academica") onNavigate("carga_academica") }
        )
    }
}
