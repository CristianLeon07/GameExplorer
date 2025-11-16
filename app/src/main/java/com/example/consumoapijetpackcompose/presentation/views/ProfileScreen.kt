package com.example.consumoapijetpackcompose.presentation.views

import android.Manifest
import android.content.pm.PackageManager
import coil3.compose.rememberAsyncImagePainter
import com.example.consumoapijetpackcompose.presentation.viewmodels.ProfileViewModel
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.consumoapijetpackcompose.core.util.ResourceUiState
import com.example.consumoapijetpackcompose.presentation.navigation.Inicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Estados
    val userState by viewModel.userState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    val name by viewModel.name.collectAsState()
    val photoUri by viewModel.photoUri.collectAsState()

    // Colores desde tu paleta global
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val surface = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    // Fondo adaptado al tema
    val backgroundColor = MaterialTheme.colorScheme.background

    // BottomSheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onPhotoChange(it.toString()) }
        showSheet = false
    }

    val imageFile = remember { File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg") }
    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.onPhotoChange(imageUri.toString())
        showSheet = false
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(imageUri)
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    fun openCamera() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) cameraLauncher.launch(imageUri)
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    // UI Principal
    when (userState) {

        is ResourceUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        }

        is ResourceUiState.Success -> {

            val user = (userState as ResourceUiState.Success).data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(20.dp)
            ) {

                // FOTO DE PERFIL
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box {

                        Image(
                            painter = rememberAsyncImagePainter(photoUri ?: user.photo),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(surfaceVariant)
                                .border(4.dp, backgroundColor, CircleShape)
                                .shadow(10.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // BOTÓN EDITAR FOTO
                        IconButton(
                            onClick = { showSheet = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(42.dp)
                                .background(primary, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar foto",
                                tint = onPrimary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                // CARD INFORMACIÓN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = surface),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        Text(
                            text = "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            color = primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { Text("Nombre completo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primary,
                                focusedLabelColor = primary
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = user.email ?: "",
                            onValueChange = {},
                            label = { Text("Correo electrónico") },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))

                // BOTÓN GUARDAR
                Button(
                    onClick = { viewModel.updateUser() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    Text("Guardar cambios", color = onPrimary)
                }
            }

            // RESULTADO POST-UPDATE
            when (updateState) {
                is ResourceUiState.Success -> {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    viewModel.resetUpdateState()
                    navController.navigate(Inicio) {
                        popUpTo("profile") { inclusive = true }
                    }
                }

                is ResourceUiState.Error -> {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    viewModel.resetUpdateState()
                }

                else -> {}
            }
        }

        is ResourceUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar los datos")
            }
        }

        ResourceUiState.Idle -> {}
    }

    // BOTTOM SHEET
    if (showSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showSheet = false },
            containerColor = backgroundColor
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Text(
                    "Cambiar foto",
                    style = MaterialTheme.typography.titleMedium,
                    color = primary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Button(
                    onClick = { openCamera() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    Text("📷 Tomar foto", color = onPrimary)
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = { openGallery() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    Text("🖼 Elegir de galería", color = onPrimary)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primary
                    )
                ) {
                    Text("Cancelar")
                }

                Spacer(Modifier.height(25.dp))
            }
        }
    }
}


