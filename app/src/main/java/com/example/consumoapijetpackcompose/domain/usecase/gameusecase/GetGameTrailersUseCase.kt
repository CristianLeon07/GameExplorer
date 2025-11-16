package com.example.consumoapijetpackcompose.domain.usecase.gameusecase

import com.example.consumoapijetpackcompose.core.network.NetworkMonitor
import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.domain.models.TrailersList
import com.example.consumoapijetpackcompose.domain.repository.GamesRepos
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetGameTrailersUseCase @Inject constructor(
    private val repository: GamesRepos,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(gameId: Int): ResourceData<List<TrailersList>> {
        val isConnected = networkMonitor.isConnected.first()

        if (!isConnected) {
            return ResourceData.Error("No hay conexión a internet")
        }

        return try {
            // Ya devuelve Resource<List<TrailersList>>
            repository.getGameTrailer(gameId)
        } catch (e: Exception) {
            ResourceData.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}