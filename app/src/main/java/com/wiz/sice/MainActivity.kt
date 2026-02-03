package com.wiz.sice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wiz.sice.ui.theme.SiceTheme
import com.wiz.sice.ui.nav.SicenetApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiceTheme {
                SicenetApp()
            }
        }
    }
}

