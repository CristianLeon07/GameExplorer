package com.example.consumoapijetpackcompose.domain.repository

import com.example.consumoapijetpackcompose.domain.models.UserModel

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun loginWithGoogle(idToken: String): Boolean
    suspend fun logout()
    suspend fun getCurrentUser(): UserModel?
}