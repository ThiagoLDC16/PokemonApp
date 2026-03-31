package com.example.pokemonapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val description: String,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int
)

enum class PokemonType(val color: Long) {
    Fire(0xFFF08030),
    Water(0xFF6890F0),
    Grass(0xFF78C850),
    Electric(0xFFF8D030),
    Ice(0xFF98D8D8),
    Fighting(0xFFC03028),
    Poison(0xFFA040A0),
    Ground(0xFFE0C068),
    Flying(0xFFA890F0),
    Psychic(0xFFF85888),
    Bug(0xFFA8B820),
    Rock(0xFFB8A038),
    Ghost(0xFF705898),
    Dragon(0xFF7038F8),
    Steel(0xFFB8B8D0),
    Normal(0xFFA8A878);

    companion object {
        fun getColor(typeName: String): Long {
            return entries.find { it.name.equals(typeName, ignoreCase = true) }?.color ?: 0xFFA8A878
        }
    }
}
