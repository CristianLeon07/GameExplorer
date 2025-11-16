package com.example.consumoapijetpackcompose.domain.repository

import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.domain.models.TrailersList

/**
 * Capa de dominio (Domain Layer)
 * Define el contrato del repositorio.
 * Aquí solo se declaran las funciones, sin lógica ni dependencias de red.
 *
 * - El ViewModel conoce esta interfaz, no la implementación.
 * - Esto permite reemplazar la implementación (por ejemplo, por una cache local o fake repo) fácilmente.
 */

interface GamesRepos {
    /**
     * Obtiene la lista general de juegos desde la API, o desde cualquier otra fuente de dato
     */
    suspend fun getGames(): ResourceData<List<GamesList>>

    /**
     * Obtiene el detalle de un juego específico por ID. desde la API, o desde cualquier otra fuente de dato
     */
    suspend fun getGameDetail(id: Int): ResourceData<GameDetailModel?>

    /**
     * Obtiene la lista de trailers del juego seleccionado.desde la API, o desde cualquier otra fuente de dato
     */
    suspend fun getGameTrailer(id: Int): ResourceData<List<TrailersList>>


}