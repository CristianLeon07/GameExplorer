package com.example.consumoapijetpackcompose.presentation.viewmodels


import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consumoapijetpackcompose.core.util.ResourceUiState
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.UserRepository
import com.example.consumoapijetpackcompose.domain.usecase.userusecase.UpdateUserUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val updateUserUseCase: UpdateUserUseCase,
    private val firebaseAuth: FirebaseAuth
) : AndroidViewModel(application = application) {

    private val _userState = MutableStateFlow<ResourceUiState<UserModel>>(ResourceUiState.Loading)
    val userState: StateFlow<ResourceUiState<UserModel>> = _userState

    private val _updateState = MutableStateFlow<ResourceUiState<Unit>>(ResourceUiState.Idle)
    val updateState: StateFlow<ResourceUiState<Unit>> = _updateState

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _photoUri = MutableStateFlow<String?>(null)
    val photoUri: StateFlow<String?> = _photoUri

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _userState.value = ResourceUiState.Loading

            // 1️⃣ Intentar cargar usuario local (Room)
            val localUser = userRepository.getLoggedInUser()

            if (localUser != null) {
                _name.value = localUser.name
                _photoUri.value = localUser.photo
                _userState.value = ResourceUiState.Success(localUser)
                return@launch
            }

            // 2️⃣ Si no hay local -> cargar Firebase
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val user = UserModel(
                    name = firebaseUser.displayName ?: "Usuario",
                    email = firebaseUser.email ?: "",
                    password = "",
                    confPassword = null,
                    loginMethod = "google",
                    isLoggedIn = true,
                    photo = firebaseUser.photoUrl?.toString()
                )

                _name.value = user.name
                _photoUri.value = user.photo
                _userState.value = ResourceUiState.Success(user)
                return@launch
            }

            // 3️⃣ Si tampoco hay usuario en Firebase
            _userState.value = ResourceUiState.Error("No hay usuario logueado")
        }
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onPhotoChange(photo: String?) {
        viewModelScope.launch {
            if (photo != null) {

                val uri = Uri.parse(photo)

                val permanentFile = savePhotoToInternalStorage(uri)

                _photoUri.value = permanentFile.absolutePath
            }
        }
    }

    private suspend fun savePhotoToInternalStorage(uri: Uri): File {
        val context = getApplication<Application>()

        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")

        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }


    fun updateUser() {
        viewModelScope.launch {
            val currentUser = (userState.value as? ResourceUiState.Success)?.data
                ?: return@launch

            _updateState.value = ResourceUiState.Loading

            val updatedUser = currentUser.copy(
                name = _name.value,
                photo = _photoUri.value
            )

            try {
                updateUserUseCase(updatedUser)
                _updateState.value = ResourceUiState.Success(Unit)
            } catch (e: Exception) {
                _updateState.value = ResourceUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = ResourceUiState.Idle
    }
}

