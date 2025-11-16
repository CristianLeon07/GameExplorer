package com.example.consumoapijetpackcompose.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.consumoapijetpackcompose.domain.repository.AuthRepository
import com.example.consumoapijetpackcompose.presentation.navigation.Inicio
import com.example.consumoapijetpackcompose.presentation.navigation.Login
import com.example.consumoapijetpackcompose.presentation.navigation.Splash
import com.example.consumoapijetpackcompose.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Observa el estado de usuario y carga
    val isLoading = authViewModel.isLoading.value
    val currentUser = authViewModel.currentUser.value


    // Ejecuta la lógica al iniciar el Splash
    LaunchedEffect(Unit) {
        // Pequeña pausa para el efecto visual del splash
        delay(1000)

        // Refresca el usuario actual desde Room/Firebase
        authViewModel.refreshUser()
    }

    // Navegación automática cuando termina la carga
    LaunchedEffect(currentUser, isLoading) {
        if (!isLoading) {
            if (authViewModel.isLoggedIn()) {
                navController.navigate(Inicio) {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate(Login) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // UI del Splash
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}


