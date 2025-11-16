package com.example.consumoapijetpackcompose.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.consumoapijetpackcompose.presentation.viewmodels.AuthViewModel
import com.example.consumoapijetpackcompose.presentation.viewmodels.GamesDetailViewModel
import com.example.consumoapijetpackcompose.presentation.viewmodels.HomeViewModel
import com.example.consumoapijetpackcompose.presentation.viewmodels.LoginViewModel
import com.example.consumoapijetpackcompose.presentation.viewmodels.ProfileViewModel
import com.example.consumoapijetpackcompose.presentation.viewmodels.RegisterViewModel
import com.example.consumoapijetpackcompose.presentation.views.GameDetailScreen
import com.example.consumoapijetpackcompose.presentation.views.HomeScreen
import com.example.consumoapijetpackcompose.presentation.views.LoginScreen
import com.example.consumoapijetpackcompose.presentation.views.ProfileScreen
import com.example.consumoapijetpackcompose.presentation.views.RegisterScreen
import com.example.consumoapijetpackcompose.presentation.views.SplashScreen

@Composable
fun NavManager() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Splash) {

        // SplashScreen
        composable<Splash> {
            val viewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
            SplashScreen(navController = navController, viewModel)
        }

        // LoginScreen
        composable<Login> {
            val loginViewModel: LoginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        // RegisterScreen
        composable<Register> {
            val registerViewModel: RegisterViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(navController = navController, viewModel = registerViewModel)
        }

        // HomeScreen
        composable<Inicio> {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(navController = navController, homeViewModel = homeViewModel )
        }

        //ProfileScreen
        composable<Profile> {
            val profileViewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>()
            ProfileScreen(navController = navController, viewModel = profileViewModel)
        }

        // GameDetailScreen
        composable<Detail> { args ->
            val destination = args.toRoute<Detail>()
            val detailViewModel: GamesDetailViewModel = hiltViewModel()
            GameDetailScreen(
                id = destination.id,
                navController = navController,
                viewModel = detailViewModel
            )
        }
    }
}
