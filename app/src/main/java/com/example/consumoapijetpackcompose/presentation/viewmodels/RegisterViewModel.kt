package com.example.consumoapijetpackcompose.presentation.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consumoapijetpackcompose.core.util.ResourceUiState
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.usecase.userusecase.AddUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val addUserUseCase: AddUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResourceUiState<Unit>>(ResourceUiState.Idle)
    val uiState: StateFlow<ResourceUiState<Unit>> = _uiState

    fun onUserRegister(email: String, name: String, password: String, confPassword: String) {
        viewModelScope.launch {
            when {
                email.isEmpty() || name.isEmpty() || password.isEmpty() || confPassword.isEmpty() -> {
                    _uiState.value = ResourceUiState.Error("Por favor completa todos los campos")
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    _uiState.value = ResourceUiState.Error("Correo inválido")
                }
                password != confPassword -> {
                    _uiState.value = ResourceUiState.Error("Las contraseñas no coinciden")
                }
                else -> {
                    _uiState.value = ResourceUiState.Loading
                    try {
                        addUserUseCase(UserModel(email, name, password, confPassword))
                        _uiState.value = ResourceUiState.Success(Unit)
                    } catch (e: Exception) {
                        _uiState.value = ResourceUiState.Error(
                            "Error al registrar: ${e.message ?: "Desconocido"}"
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = ResourceUiState.Idle
    }
}

