package com.example.consumoapijetpackcompose.domain.repository

import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.models.GamesList
import com.example.consumoapijetpackcompose.domain.models.TrailersList
import com.example.consumoapijetpackcompose.data.remote.APIGames
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val apiGames: APIGames
) {


    suspend fun getGames(): List<GamesList> {

        val response = apiGames.getGames()
        return if (response.isSuccessful) response.body()?.listGames ?: emptyList()
        else emptyList()
    }

    suspend fun getGameDetail(id: Int): GameDetailModel? {
        val response = apiGames.getGameDetail(id = id)
        return if (response.isSuccessful) response.body()
        else null
    }


    suspend fun getGameTrailer(id: Int): List<TrailersList> {
        val response = apiGames.getGamesTrailer(id)
        return if (response.isSuccessful) {
            response.body()?.results ?: emptyList()
        } else emptyList()
    }
}