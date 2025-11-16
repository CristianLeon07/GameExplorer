package com.example.consumoapijetpackcompose.data.remote

import com.example.consumoapijetpackcompose.core.util.Constantes
import com.example.consumoapijetpackcompose.domain.models.GameDetailModel
import com.example.consumoapijetpackcompose.domain.models.GamesModel
import com.example.consumoapijetpackcompose.domain.models.GamesTrailerModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIGames {

    @GET("games${Constantes.API_KEY}")
    suspend fun getGames(): Response<GamesModel>

    @GET("games/{id}${Constantes.API_KEY}")
    suspend fun getGameDetail(
        @Path("id") id: Int
    ): Response<GameDetailModel>

    @GET("games/{id}/movies${Constantes.API_KEY}")
    suspend fun getGamesTrailer(@Path("id") id: Int
    ): Response<GamesTrailerModel>

}