package com.example.pokemonapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val results: List<PokemonBriefResponse>
)

@Serializable
data class PokemonBriefResponse(
    val name: String,
    val url: String
) {
    val id: Int
        get() {
            return try {
                // ID extraction: handles both "baseUrl/id/" and "baseUrl/id"
                url.trimEnd('/').split('/').last().toInt()
            } catch (e: Exception) {
                0
            }
        }
    
    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}

@Serializable
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlotResponse>,
    val stats: List<StatSlotResponse>,
    val sprites: SpritesResponse
)

@Serializable
data class TypeSlotResponse(
    val type: TypeResponse
)

@Serializable
data class TypeResponse(
    val name: String
)

@Serializable
data class TypeDetailResponse(
    val name: String,
    val pokemon: List<TypePokemonSlotResponse>
)

@Serializable
data class TypePokemonSlotResponse(
    val pokemon: PokemonBriefResponse
)

@Serializable
data class StatSlotResponse(
    @SerialName("base_stat") val baseStat: Int,
    val stat: StatResponse
)

@Serializable
data class StatResponse(
    val name: String
)

@Serializable
data class SpritesResponse(
    val other: OtherSpritesResponse
)

@Serializable
data class OtherSpritesResponse(
    @SerialName("official-artwork") val officialArtwork: OfficialArtworkResponse
)

@Serializable
data class OfficialArtworkResponse(
    @SerialName("front_default") val frontDefault: String
)
