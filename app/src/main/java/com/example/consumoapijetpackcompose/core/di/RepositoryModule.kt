package com.example.consumoapijetpackcompose.core.di

import com.example.consumoapijetpackcompose.data.repository.GamesRepositoryImpl
import com.example.consumoapijetpackcompose.domain.repository.GamesRepos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //vincula la interfaz con su implementacion

    @Binds
    @Singleton
    abstract fun bindGamesRepository(
        impl: GamesRepositoryImpl
    ): GamesRepos
}