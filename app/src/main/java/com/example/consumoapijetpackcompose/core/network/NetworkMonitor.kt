package com.example.consumoapijetpackcompose.core.network


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clase que expone un flujo observable de conectividad.
 * Puede inyectarse en cualquier capa (por ejemplo, en ViewModel o UseCase).
 */
@Singleton
class NetworkMonitor @Inject constructor(
    private val connectivityObserver: NetworkConnectivityObserver
) {
    val isConnected: Flow<Boolean> = connectivityObserver.observe()
}
