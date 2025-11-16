package com.example.consumoapijetpackcompose.core.util

sealed class ResourceData<out T> {
    data class Success<out T>(val data: T) : ResourceData<T>()
    data class Error(val message: String, val code: Int? = null) : ResourceData<Nothing>()
    object Loading : ResourceData<Nothing>()
}

