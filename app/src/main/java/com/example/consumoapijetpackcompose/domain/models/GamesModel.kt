package com.example.consumoapijetpackcompose.domain.models

import com.google.gson.annotations.SerializedName

data class GamesModel(
    @SerializedName("counts")
    val total: Int,
    @SerializedName("results")
    val listGames: List<GamesList>
)

data class GamesList(
    val id: Int,
    val name: String,
    @SerializedName("background_image")
    val imagen: String
)

data class GameDetailModel(
    val id: Int,
    val name: String,
    val description: String,
    val released: String,
    val background_image_additional: String,
    val website: String,
    val rating: Double
)

data class GamesTrailerModel(

    @SerializedName("results")
    val results: List<TrailersList>
)

data class TrailersList(
    val id: Int,
    val name: String,
    val preview: String,
    val data: DataTrailer
)

data class DataTrailer(
    @SerializedName("480")
    val calidad480: String,
    val max: String
)







