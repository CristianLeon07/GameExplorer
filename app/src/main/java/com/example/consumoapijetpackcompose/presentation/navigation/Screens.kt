package com.example.consumoapijetpackcompose.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Register

@Serializable
object Inicio

@Serializable
object Profile

@Serializable
data class Detail(val id: Int)