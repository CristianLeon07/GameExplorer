package com.example.consumoapijetpackcompose.domain.usecase.gameusecase

import com.example.consumoapijetpackcompose.core.network.NetworkMonitor
import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.repository.GamesRepos
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetGameDetailUseCase @Inject constructor(
    private val repository: GamesRepos,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(gameId: Int): ResourceData<GameDetailModel?> {
        val isConnected = networkMonitor.isConnected.first()

        if (!isConnected) {
            return ResourceData.Error("No hay conexión a internet")
        }

        return try {
            // Ya devuelve Resource<GameDetailModel?>
            repository.getGameDetail(gameId)
        } catch (e: Exception) {
            ResourceData.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}