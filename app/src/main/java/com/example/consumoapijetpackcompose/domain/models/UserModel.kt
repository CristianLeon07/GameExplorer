package com.example.consumoapijetpackcompose.domain.models

data class UserModel(
    val email: String,
    val name: String,
    val password: String = "",
    val confPassword: String? = null,
    val loginMethod: String = "local", // "local" o "google"
    val isLoggedIn: Boolean = false,   // indica si este usuario tiene la sesión activa
    val photo: String? = null          // solo para usuarios de Google
)

