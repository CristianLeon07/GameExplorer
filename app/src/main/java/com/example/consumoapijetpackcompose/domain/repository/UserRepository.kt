package com.example.consumoapijetpackcompose.domain.repository

import com.example.consumoapijetpackcompose.domain.models.UserModel
import kotlinx.coroutines.flow.Flow


interface UserRepository {
    val users: Flow<List<UserModel>>

    suspend fun addUser(user: UserModel)
    suspend fun updateUser(user: UserModel)
    suspend fun deleteUser(user: UserModel)
    suspend fun getUserByEmail(email: String): UserModel?
    suspend fun getLoggedInUser(): UserModel?
    suspend fun setUserLoggedIn(email: String)
    suspend fun clearSession()
}
