package com.example.consumoapijetpackcompose.core.di

import com.example.consumoapijetpackcompose.data.repository.AuthRepositoryImpl
import com.example.consumoapijetpackcompose.data.repository.UserRepositoryImpl
import com.example.consumoapijetpackcompose.domain.repository.AuthRepository
import com.example.consumoapijetpackcompose.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}

