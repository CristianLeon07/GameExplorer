package com.example.consumoapijetpackcompose.presentation.views


import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.media3.common.MediaItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.text.HtmlCompat
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.consumoapijetpackcompose.core.util.Constantes
import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.models.TrailersList
import com.example.consumoapijetpackcompose.R
import com.example.consumoapijetpackcompose.presentation.viewmodels.GamesDetailViewModel

import androidx.compose.animation.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    id: Int,
    viewModel: GamesDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    val gameDetailState by viewModel.gameDetail.collectAsState()
    val trailersState by viewModel.trailers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadGameDetailAndTrailers(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Juego") },
                navigationIcon = {
                    IconButton(onClick = {
                        // marcamos que debe limpiarse si es necesario y salimos
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("shouldClearSearch", true)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->

        // Fondo con degradado oscuro tipo "gaming"
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF070709),
                            Color(0xFF0F1113),
                            Color(0xFF060607)
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {
            AnimatedContent(
                targetState = Triple(isLoading, errorMessage, gameDetailState),
                label = "GameDetailTransition",
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) }
            ) { (loading, error, detail) ->
                when {
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF64B5F6))
                        }
                    }

                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = error, color = Color.Red)
                        }
                    }

                    detail != null -> {
                        GameDetailContentAnimated(
                            gameDetail = detail,
                            trailers = trailersState
                        )
                    }

                    else -> {
                        // estado vacío
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No hay datos", color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}


// ------------------------------
// Contenido animado y layout
// ------------------------------
@Composable
fun GameDetailContentAnimated(
    gameDetail: GameDetailModel,
    trailers: List<TrailersList>
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 3 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Encabezado con imagen, overlay y título
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = gameDetail.background_image_additional,
                    contentDescription = gameDetail.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay degradado inferior para lectura
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xBB000000)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Información encima de la imagen
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = gameDetail.name,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Rating chip
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${gameDetail.rating}",
                                    color = Color(0xFFBBDEFB),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        // Fecha / Metadato si existe
                        if (!gameDetail.released.isNullOrBlank()) {
                            Text(
                                text = " • ${gameDetail.released}",
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Card principal con contenido
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Descripción
                    Text(
                        text = "Descripción",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(10.dp))

                    val descriptionText = remember(gameDetail.description) {
                        HtmlCompat.fromHtml(
                            gameDetail.description,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ).toString()
                    }

                    Text(
                        text = descriptionText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.LightGray,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    // Imágenes adicionales si hay
                    if (!gameDetail.background_image_additional.isNullOrBlank()) {
                        Text(
                            text = "Imágenes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        GameImagesSlider(gameDetail)
                        Spacer(Modifier.height(12.dp))
                    }

                    // Trailers
                    if (trailers.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Tráilers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f))
                        ) {
                            TrailerAutoPlayer(trailers)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}


// ------------------------------
// Reproductor de trailers (ExoPlayer)
// ------------------------------
@Composable
fun TrailerAutoPlayer(trailers: List<TrailersList>) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    // Construimos lista de URIs válidas
    val trailerUris = remember(trailers) {
        trailers.mapNotNull { t ->
            // priorizamos calidad mayor, fallback a calidad480
            val url = t.data.max.ifBlank { t.data.calidad480 }.ifBlank { null }
            url
        }.map { Uri.parse(it) }
    }

    LaunchedEffect(trailerUris) {
        if (trailerUris.isEmpty()) return@LaunchedEffect
        val mediaItems = trailerUris.map { uri -> MediaItem.fromUri(uri) }
        player.setMediaItems(mediaItems)
        player.prepare()
        player.playWhenReady = true

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED && player.mediaItemCount > 0) {
                    val nextIndex = (player.currentMediaItemIndex + 1) % player.mediaItemCount
                    player.seekTo(nextIndex, 0L)
                    player.playWhenReady = true
                }
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    )
}


// ------------------------------
// Slider de imágenes (mejorado)
// ------------------------------
@Composable
fun GameImagesSlider(gameDetail: GameDetailModel) {
    // Si tu API envia varias imágenes en un campo, adáptalo. Aquí asumo un único URL adicional separado.
    val images = listOfNotNull(gameDetail.background_image_additional).ifEmpty {
        listOfNotNull(gameDetail.background_image_additional)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        images.forEach { image ->
            AsyncImage(
                model = image,
                contentDescription = gameDetail.name,
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
