package com.example.consumoapijetpackcompose.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.consumoapijetpackcompose.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ------------------------------
    // Consultas generales
    // ------------------------------

    // Obtener todos los usuarios
    @Query("SELECT * FROM users")
    fun getUsers(): Flow<List<UserEntity>>

    // Obtener usuario por email y contraseña (login local)
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    // Obtener usuario por email (para login con Google o verificación general)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Insertar nuevo usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserEntity)

    // Actualizar usuario existente
    @Update
    suspend fun updateUser(user: UserEntity)

    // Eliminar usuario
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // ------------------------------
    // Manejo de sesión local
    // ------------------------------

    // Obtener el usuario actualmente logueado
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): UserEntity?

    // Marcar todos los usuarios como deslogueados
    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun clearAllSessions()

    // Marcar un usuario como logueado
    @Query("UPDATE users SET isLoggedIn = 1 WHERE email = :email")
    suspend fun setUserLoggedIn(email: String)
}