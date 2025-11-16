package com.example.consumoapijetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.consumoapijetpackcompose.presentation.navigation.NavManager
import com.example.consumoapijetpackcompose.ui.theme.ConsumoApiJetPackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // ¡PASO CRÍTICO! Llama a installSplashScreen() ANTES de super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Opcional: Controla cuándo quitar el Splash (útil si cargas datos iniciales)
        // Por ahora, lo dejamos para que se quite inmediatamente después de cargar Compose.
        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            ConsumoApiJetPackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavManager()
                }
            }
        }
    }
}
