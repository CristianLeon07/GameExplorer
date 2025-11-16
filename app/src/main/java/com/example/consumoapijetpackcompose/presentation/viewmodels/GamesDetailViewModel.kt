package com.example.consumoapijetpackcompose.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consumoapijetpackcompose.core.util.ResourceData
import com.example.consumoapijetpackcompose.domain.usecase.gameusecase.GetGameTrailersUseCase
import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.models.TrailersList
import com.example.consumoapijetpackcompose.domain.usecase.gameusecase.GetGameDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GamesDetailViewModel @Inject constructor(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val getGameTrailersUseCase: GetGameTrailersUseCase
) : ViewModel() {

    // Estado del detalle del juego
    private val _gameDetail = MutableStateFlow<GameDetailModel?>(null)
    val gameDetail: StateFlow<GameDetailModel?> = _gameDetail.asStateFlow()

    // Estado de los trailers del juego
    private val _trailers = MutableStateFlow<List<TrailersList>>(emptyList())
    val trailers: StateFlow<List<TrailersList>> = _trailers.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Carga en paralelo el detalle del juego y los trailers usando los UseCases,
     * adaptado a Resource<T>.
     */
    fun loadGameDetailAndTrailers(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Ejecutar ambas llamadas en paralelo
                val detailResultDeferred = async { getGameDetailUseCase(id) }
                val trailersResultDeferred = async { getGameTrailersUseCase(id) }

                // ---- Manejo del detalle ----
                when (val result = detailResultDeferred.await()) {
                    is ResourceData.Success -> _gameDetail.value = result.data
                    is ResourceData.Error -> _errorMessage.value =
                        "Error al cargar detalle: ${result.message ?: "Error desconocido"}"
                    is ResourceData.Loading -> { /* No se usa aquí directamente */ }
                }

                // ---- Manejo de los trailers ----
                when (val result = trailersResultDeferred.await()) {
                    is ResourceData.Success -> _trailers.value = result.data
                    is ResourceData.Error -> _trailers.value = emptyList() // si falla, mostramos vacío
                    is ResourceData.Loading -> { }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.localizedMessage ?: "Desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}