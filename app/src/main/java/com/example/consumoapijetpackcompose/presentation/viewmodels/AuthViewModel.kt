package com.example.consumoapijetpackcompose.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.consumoapijetpackcompose.domain.usecase.userusecase.GetCurrentUserUseCase
import com.example.consumoapijetpackcompose.domain.usecase.userusecase.LoginUseCase
import com.example.consumoapijetpackcompose.domain.usecase.userusecase.LogoutUseCase
import kotlinx.coroutines.launch


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    // ------------------------
    // Estado del usuario actual
    // ------------------------
    private val _currentUser = mutableStateOf<UserModel?>(null)
    val currentUser: State<UserModel?> = _currentUser

    // ------------------------
    // Estado de carga
    // ------------------------
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        refreshUser()
    }

    /** Refresca el usuario actual desde Room o Firebase */
    fun refreshUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _currentUser.value = getCurrentUserUseCase()
            _isLoading.value = false
        }
    }

    /** Login local con email y contraseña */
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = loginUseCase(email = email, password = password)
            _currentUser.value = if (success) getCurrentUserUseCase() else null
            _isLoading.value = false
            onResult(success)
        }
    }

    /** Login con Google usando idToken */
    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = loginUseCase(idToken = idToken)
            _currentUser.value = if (success) getCurrentUserUseCase() else null
            _isLoading.value = false
            onResult(success)
        }
    }

    /** Logout completo centralizado */
    fun logout(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            logoutUseCase()               // llama al repositorio centralizado
            _currentUser.value = null
            _isLoading.value = false
            onComplete?.invoke()          // navegación o acciones adicionales
        }
    }

    /** Devuelve true si hay un usuario activo */
    fun isLoggedIn(): Boolean = _currentUser.value != null

    /** Devuelve el usuario actual de manera segura */
    fun getCurrentUser(): UserModel? = _currentUser.value
}
