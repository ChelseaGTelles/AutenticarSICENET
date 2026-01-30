package com.example.autenticarsicenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.autenticarsicenet.ui.NavGraph
import com.example.autenticarsicenet.ui.theme.AutenticarSICENETTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutenticarSICENETTheme {
                NavGraph()
            }
        }
    }
}