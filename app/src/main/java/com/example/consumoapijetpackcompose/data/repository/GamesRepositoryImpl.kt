package com.example.consumoapijetpackcompose.data.repository

import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.data.remote.APIGames
import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.domain.models.TrailersList
import com.example.consumoapijetpackcompose.domain.repository.GamesRepos
import javax.inject.Inject

/**
 * Capa de datos (Data Layer)
 * Implementación concreta del repositorio que se conecta con la API remota (Retrofit).
 *
 * - Esta clase usa la interfaz APIGames para hacer las peticiones HTTP.
 * - Retorna datos ya "limpios", listos para que el ViewModel los use.
 */

class GamesRepositoryImpl @Inject constructor(
    private val apiGames: APIGames
) : GamesRepos {

    override suspend fun getGames(): ResourceData<List<GamesList>> {
        return try {
            val response = apiGames.getGames()
            if (response.isSuccessful) {
                ResourceData.Success(response.body()?.listGames ?: emptyList())
            } else {
                ResourceData.Error("Error ${response.code()}: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            ResourceData.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun getGameDetail(id: Int): ResourceData<GameDetailModel?> {
        return try {
            val response = apiGames.getGameDetail(id)
            if (response.isSuccessful) {
                ResourceData.Success(response.body())
            } else {
                ResourceData.Error("Error ${response.code()}: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            ResourceData.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun getGameTrailer(id: Int): ResourceData<List<TrailersList>> {
        return try {
            val response = apiGames.getGamesTrailer(id)
            if (response.isSuccessful) {
                ResourceData.Success(response.body()?.results ?: emptyList())
            } else {
                ResourceData.Error("Error ${response.code()}: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            ResourceData.Error("Error de conexión: ${e.message}")
        }
    }
}