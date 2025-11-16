package com.example.consumoapijetpackcompose.presentation.views


import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import com.example.consumoapijetpackcompose.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.consumoapijetpackcompose.presentation.components.CardGames
import com.example.consumoapijetpackcompose.presentation.components.HandleClearSearchOnReturn
import com.example.consumoapijetpackcompose.presentation.components.MainTopBar
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.presentation.navigation.Detail
import com.example.consumoapijetpackcompose.presentation.navigation.Login
import com.example.consumoapijetpackcompose.presentation.navigation.Profile
import com.example.consumoapijetpackcompose.presentation.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import com.example.consumoapijetpackcompose.core.util.ResourceUiState
import com.example.consumoapijetpackcompose.presentation.viewmodels.AuthViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    HandleClearSearchOnReturn(navController, homeViewModel)

    val uiState by homeViewModel.uiState.collectAsState()
    val user by homeViewModel.user.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf("Home", "Perfil", "Cerrar sesión")

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                drawerItems = drawerItems,
                drawerState = drawerState,
                user = user,
                navController = navController,
                authViewModel = authViewModel
            )
        },
        content = {
            HomeScaffold(
                drawerState = drawerState,
                scope = scope,
                user = user,
                uiState = uiState,
                homeViewModel = homeViewModel,
                navController = navController
            )
        }
    )
}

// ----------------------------------------------------------------------
// DRAWER
// ----------------------------------------------------------------------

@Composable
fun DrawerContent(
    drawerItems: List<String>,
    drawerState: DrawerState,
    user: UserModel?,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.78f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {

            DrawerHeader(user)

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 👉 Menú principal (solo Home y Perfil)
            DrawerMenu(
                navController = navController,
                drawerState = drawerState,
                drawerItems = drawerItems
            )

            // 👉 Esto empuja el botón de cerrar sesión al fondo
            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            DrawerLogout(
                drawerState = drawerState,
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
private fun DrawerHeader(user: UserModel?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user?.photo != null) {
            AsyncImage(
                model = user.photo,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(14.dp))

        Column {
            Text(
                text = user?.name ?: "Invitado",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = user?.email ?: "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerMenu(
    navController: NavController,
    drawerState: DrawerState,
    drawerItems: List<String>
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
    ) {

        val mainItems = drawerItems.filter { it != "Cerrar sesión" }

        mainItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    when (item) {
                        "Home" -> Icon(Icons.Default.Home, contentDescription = null)
                        "Perfil" -> Icon(Icons.Default.Person, contentDescription = null)
                    }
                },
                label = { Text(item) },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    if (item == "Perfil") navController.navigate(Profile)
                }
            )
        }
    }
}

@Composable
private fun DrawerLogout(
    drawerState: DrawerState,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()

    NavigationDrawerItem(
        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
        label = { Text("Cerrar sesión") },
        selected = false,
        onClick = {
            scope.launch { drawerState.close() }

            // Usamos el AuthViewModel para logout completo
            authViewModel.logout {
                navController.navigate(Login) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.errorContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onErrorContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

// ----------------------------------------------------------------------
// SCAFFOLD
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScaffold(
    drawerState: DrawerState,
    scope: CoroutineScope,
    user: UserModel?,
    uiState: ResourceUiState<List<GamesList>>,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API JUEGOS") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (!user?.name.isNullOrEmpty()) {
                Text(
                    text = "Hola, ${user?.name}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }

            SearchGame(homeViewModel)
            Spacer(Modifier.height(8.dp))

            HandleUiState(uiState, homeViewModel, navController)
        }
    }
}

// ----------------------------------------------------------------------
// SEARCH BAR
// ----------------------------------------------------------------------

@Composable
fun SearchGame(homeViewModel: HomeViewModel) {
    val searchText by homeViewModel.searchText.collectAsState()
    val containerColor = MaterialTheme.colorScheme.surfaceVariant

    OutlinedTextField(
        value = searchText,
        onValueChange = homeViewModel::onSearchTextChange,
        placeholder = { Text("Buscar juego", fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

// ----------------------------------------------------------------------
// UI STATES
// ----------------------------------------------------------------------

@Composable
fun HandleUiState(
    uiState: ResourceUiState<List<GamesList>>,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    when (uiState) {
        is ResourceUiState.Idle -> {}
        is ResourceUiState.Loading -> LoadingView()
        is ResourceUiState.Success ->{
            if (uiState.data.isEmpty()){
                EmptySearchResult()
            }else{
                GamesGrid(uiState.data, navController)
            }

        }
        is ResourceUiState.Error -> ErrorView(uiState.message) { homeViewModel.loadGames() }
    }
}

// ----------------------------------------------------------------------
// GRID
// ----------------------------------------------------------------------

@Composable
fun GamesGrid(
    games: List<GamesList>,
    navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(games, key = { it.id }) { game ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                GameCardItem(game) { navController.navigate(Detail(game.id)) }
            }
        }
    }
}

@Composable
fun GameCardItem(game: GamesList, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CardGames(game = game, onClick = onClick)

        Text(
            text = game.name,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
        )
    }
}

// ----------------------------------------------------------------------
// EMPTY / ERROR / LOADING
// ----------------------------------------------------------------------

@Composable
fun EmptySearchResult() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimationEmpty()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No se encontraron resultados",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun LottieAnimationEmpty(
    @RawRes lottieRes: Int = R.raw.notsearch,
    message: String = "No se encontraron resultados"
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(composition)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.height(180.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Cargando juegos...")
    }
}

@Composable
fun ErrorView(message: String, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Ocurrió un error",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(message, textAlign = TextAlign.Center)
        onRetry?.let {
            Spacer(Modifier.height(16.dp))
            Button(onClick = it) { Text("Reintentar") }
        }
    }
}
