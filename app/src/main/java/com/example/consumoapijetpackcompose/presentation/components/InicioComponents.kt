package com.example.consumoapijetpackcompose.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.example.consumoapijetpackcompose.core.util.Constantes
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(Constantes.COLOR_NEGRO_PERSONALIZADO)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
    )
}

@Composable
fun CardGames(
    game: GamesList, onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .shadow(40.dp)
            .clickable { onClick() }) {

        Column {
            InitImage(imagen = game.imagen)
        }

    }
}

@Composable
fun InitImage(imagen: String) {

    AsyncImage(
        model = imagen,
        contentDescription = "Foto",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )

}


/**
 * Observa el estado del back stack y limpia el texto de búsqueda en el ViewModel
 * cuando se navega de regreso desde el detalle hacia el Home.
 *
 * @param navController Controlador de navegación actual
 * @param viewModel ViewModel de la lista de juegos
 */

@Composable
fun HandleClearSearchOnReturn(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value

    LaunchedEffect(navBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("shouldClearSearch")
            ?.observeForever { shouldClear ->
                if (shouldClear) {
                    viewModel.clearSearch()
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("shouldClearSearch", false)
                }
            }
    }
}
