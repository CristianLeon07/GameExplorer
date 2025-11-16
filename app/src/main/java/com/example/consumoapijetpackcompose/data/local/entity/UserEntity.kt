package com.example.consumoapijetpackcompose.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val password: String? = null,
    val loginMethod: String = "local",
    val isLoggedIn: Boolean = false,
    val photo: String? = null
)
