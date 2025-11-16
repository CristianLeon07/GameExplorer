package com.example.consumoapijetpackcompose.core.di

import com.example.consumoapijetpackcompose.core.util.Constantes
import com.example.consumoapijetpackcompose.data.remote.APIGames
import com.example.consumoapijetpackcompose.domain.repository.GamesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // provee la instancia de retrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // provee la interfaz de la api

    @Provides
    @Singleton
    fun provideGamesApi(retrofit: Retrofit): APIGames =
        retrofit.create(APIGames::class.java)
}