package com.example.consumoapijetpackcompose.domain.usecase.gameusecase

import com.example.consumoapijetpackcompose.core.network.NetworkMonitor
import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.domain.repository.GamesRepos
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de juegos.
 * Aquí se puede agregar lógica adicional como:
 * - Verificar conexión a Internet
 * - Manejar errores
 * - Cachear resultados
 */

class GetGamesUseCase @Inject constructor(
    private val repository: GamesRepos,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(): ResourceData<List<GamesList>> {
        val isConnected = networkMonitor.isConnected.first()

        if (!isConnected) {
            return ResourceData.Error("No hay conexión a internet")
        }

        return try {
            when (val result = repository.getGames()) {
                is ResourceData.Success -> ResourceData.Success(result.data)
                is ResourceData.Error -> ResourceData.Error(result.message, result.code)
                else -> ResourceData.Error("Error desconocido al obtener los juegos")
            }
        } catch (e: Exception) {
            ResourceData.Error("Error inesperado: ${e.message}")
        }
    }
}