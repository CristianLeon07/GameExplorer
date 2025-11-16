package com.example.consumoapijetpackcompose.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consumoapijetpackcompose.core.util.ResourceUiState
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.UserRepository
import com.example.consumoapijetpackcompose.domain.usecase.userusecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userRepository: UserRepository // para manejar sesiones locales
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<ResourceUiState<UserModel>>(ResourceUiState.Idle)
    val uiState: StateFlow<ResourceUiState<UserModel>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Login unificado
     * @param emailValue opcional, si no se pasa se usa el estado actual
     * @param passwordValue opcional, si no se pasa se usa el estado actual
     * @param idToken opcional, si se pasa es login con Google
     */
    fun login(emailValue: String? = null, passwordValue: String? = null, idToken: String? = null) {
        viewModelScope.launch {
            val emailToUse = emailValue ?: _email.value
            val passwordToUse = passwordValue ?: _password.value

            // Validaciones solo para login local
            if (idToken == null) {
                if (emailToUse.isBlank() || passwordToUse.isBlank()) {
                    _uiEvent.emit("Por favor completa los campos")
                    return@launch
                }
                if (!isValidEmail(emailToUse)) {
                    _uiEvent.emit("Correo inválido")
                    return@launch
                }
            }

            _uiState.value = ResourceUiState.Loading

            try {
                val success = loginUseCase(emailToUse, passwordToUse, idToken)

                if (success) {
                    // Limpiar sesiones previas
                    userRepository.clearSession()

                    // Marcar usuario actual como logueado (solo local)
                    if (idToken == null) {
                        userRepository.setUserLoggedIn(emailToUse)
                    }

                    _uiState.value = ResourceUiState.Success(
                        data = userRepository.getUserByEmail(emailToUse) ?: UserModel(
                            email = emailToUse,
                            name = "",
                            password = ""
                        )
                    )
                    _uiEvent.emit(
                        if (idToken != null) "Inicio de sesión con Google exitoso"
                        else "Inicio de sesión exitoso"
                    )

                    clearFields()
                } else {
                    _uiState.value = ResourceUiState.Error("Usuario o contraseña incorrectos")
                    _uiEvent.emit("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _uiState.value = ResourceUiState.Error(e.message ?: "Error desconocido")
                _uiEvent.emit(e.message ?: "Error desconocido")
            }
        }
    }

    private fun clearFields() {
        _email.value = ""
        _password.value = ""
    }

    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

