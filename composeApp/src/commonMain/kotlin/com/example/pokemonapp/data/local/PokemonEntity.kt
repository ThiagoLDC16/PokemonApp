package com.example.pokemonapp.data.local

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_cache")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String = "" // Comma separated types
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val captureLocation: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null
)
