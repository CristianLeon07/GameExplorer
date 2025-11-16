package com.example.consumoapijetpackcompose.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.core.util.ResourceUiState
import com.example.consumoapijetpackcompose.data.repository.UserRepositoryImpl
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.UserRepository
import com.example.consumoapijetpackcompose.domain.usecase.gameusecase.GetGamesUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase,
    private val userRepository: UserRepositoryImpl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // ------------------------
    // Estado del usuario
    // ------------------------
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    // ------------------------
    // Estado de los juegos (UI)
    // ------------------------
    private val _uiState = MutableStateFlow<ResourceUiState<List<GamesList>>>(ResourceUiState.Idle)
    val uiState: StateFlow<ResourceUiState<List<GamesList>>> = _uiState.asStateFlow()

    // ------------------------
    // Búsqueda
    // ------------------------
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                loadUser()
                loadGames()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error en init: ${e.message}")
            }
        }
    }

    // ----------------------------------------------------------
    // 👤 CARGAR USUARIO
    // ----------------------------------------------------------
    private suspend fun loadUser() {
        try {
            val localUser = userRepository.getLoggedInUser()
            if (localUser != null) {
                _user.value = localUser
                Log.d("HomeViewModel", "Usuario cargado desde Room -> ${localUser.name}")
            } else {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    _user.value = UserModel(
                        name = firebaseUser.displayName ?: "Usuario",
                        email = firebaseUser.email ?: "",
                        password = "",
                        photo = firebaseUser.photoUrl?.toString(),
                        isLoggedIn = true
                    )
                    Log.d("HomeViewModel", "Usuario cargado desde Firebase -> ${firebaseUser.displayName}")
                } else {
                    Log.d("HomeViewModel", "No hay usuario logueado")
                }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error al cargar usuario: ${e.message}")
        }
    }

    // ----------------------------------------------------------
    // 2️⃣ LÓGICA DE JUEGOS (API)
    // ----------------------------------------------------------
    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
        applyFilter()
    }

    fun clearSearch() {
        _searchText.value = ""
        applyFilter()
    }

    fun loadGames() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ResourceUiState.Loading
            try {
                when (val result = getGamesUseCase()) {
                    is ResourceData.Success -> {
                        val data = result.data ?: emptyList()
                        _uiState.value = ResourceUiState.Success(data)
                    }
                    is ResourceData.Error -> {
                        _uiState.value = ResourceUiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error en loadGames: ${e.message}")
                _uiState.value = ResourceUiState.Error(e.message ?: "Error inesperado")
            }
        }
    }

    private fun applyFilter() {
        val query = _searchText.value
        val currentGames = (_uiState.value as? ResourceUiState.Success)?.data ?: emptyList()
        val filtered = if (query.isBlank()) currentGames else currentGames.filter { it.name.contains(query, ignoreCase = true) }

        _uiState.value = if (filtered.isEmpty() && currentGames.isNotEmpty()) {
            ResourceUiState.Error("No se encontraron resultados") // o puedes crear un estado específico si quieres
        } else {
            ResourceUiState.Success(filtered)
        }
    }
}


