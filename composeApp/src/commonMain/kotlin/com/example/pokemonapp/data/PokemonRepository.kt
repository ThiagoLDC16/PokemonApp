package com.example.pokemonapp.data

import com.example.pokemonapp.data.local.AppDatabase
import com.example.pokemonapp.data.local.FavoriteEntity
import com.example.pokemonapp.data.local.PokemonEntity
import com.example.pokemonapp.data.remote.PokemonDetailResponse
import com.example.pokemonapp.data.remote.PokemonListResponse
import com.example.pokemonapp.data.remote.TypeDetailResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PokemonRepository(private val database: AppDatabase) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val dao = database.pokemonDao()

    suspend fun syncIfNecessary() = withContext(Dispatchers.IO) {
        val count = dao.getCount()
        val missingTypesCount = dao.getCountMissingTypes()
        
        // Se o banco estiver vazio OU mais da metade estiver sem tipos, sincronizamos tudo
        if (count == 0 || missingTypesCount > count / 2) {
            try {
                // 1. Busca lista base (1000 pokemons)
                val response: PokemonListResponse = client.get("https://pokeapi.co/api/v2/pokemon?limit=1000").body()
                val entitiesMap = response.results.associateBy({ it.id }, {
                    PokemonEntity(
                        id = it.id,
                        name = it.name.replaceFirstChar { char -> char.uppercase() },
                        imageUrl = it.imageUrl,
                        types = ""
                    )
                }).toMutableMap()

                // 2. Busca detalhes de tipos em paralelo
                val typesList = listOf(
                    "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground",
                    "flying", "psychic", "bug", "rock", "ghost", "dragon", "steel", "normal", "fairy", "dark"
                )

                val typeDetails = typesList.map { typeName ->
                    async {
                        try {
                            client.get("https://pokeapi.co/api/v2/type/$typeName").body<TypeDetailResponse>()
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll().filterNotNull()

                // 3. Processa os resultados de tipos (sequencialmente para evitar problemas de concorrência no Map)
                typeDetails.forEach { typeDetail ->
                    val formattedType = typeDetail.name.replaceFirstChar { it.uppercase() }
                    typeDetail.pokemon.forEach { slot ->
                        val id = slot.pokemon.id
                        entitiesMap[id]?.let { entity ->
                            val currentTypes = if (entity.types.isEmpty()) {
                                formattedType
                            } else if (!entity.types.contains(formattedType)) {
                                "${entity.types},$formattedType"
                            } else {
                                entity.types
                            }
                            entitiesMap[id] = entity.copy(types = currentTypes)
                        }
                    }
                }

                // 4. Salva tudo no banco
                dao.insertAll(entitiesMap.values.toList())
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun getPagedPokemon(query: String, type: String?, page: Int, pageSize: Int): List<Pokemon> = withContext(Dispatchers.IO) {
        val offset = page * pageSize
        val dbQuery = "%$query%"
        val dbType = if (type != null) "%$type%" else null
        
        dao.getPagedPokemon(dbQuery, dbType, pageSize, offset).map {
            Pokemon(
                id = it.id, 
                name = it.name, 
                imageUrl = it.imageUrl,
                types = it.types.split(",").filter { t -> t.isNotBlank() }
            )
        }
    }

    suspend fun getPokemonDetails(id: Int): Pokemon = withContext(Dispatchers.IO) {
        val response: PokemonDetailResponse = client.get("https://pokeapi.co/api/v2/pokemon/$id").body()
        val types = response.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } }
        
        // Atualiza cache local com os tipos reais quando os detalhes são abertos
        dao.updateTypes(id, types.joinToString(","))
        
        Pokemon(
            id = response.id,
            name = response.name.replaceFirstChar { char -> char.uppercase() },
            imageUrl = response.sprites.other.officialArtwork.frontDefault,
            types = types,
            hp = response.stats.find { it.stat.name == "hp" }?.baseStat ?: 0,
            attack = response.stats.find { it.stat.name == "attack" }?.baseStat ?: 0,
            defense = response.stats.find { it.stat.name == "defense" }?.baseStat ?: 0,
            speed = response.stats.find { it.stat.name == "speed" }?.baseStat ?: 0,
            description = "Height: ${response.height / 10.0}m, Weight: ${response.weight / 10.0}kg"
        )
    }

    suspend fun saveFavorite(pokemon: Pokemon, location: String) = withContext(Dispatchers.IO) {
        dao.insertFavorite(
            FavoriteEntity(
                id = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl,
                captureLocation = location
            )
        )
    }

    suspend fun getFavorites(): List<Pokemon> = withContext(Dispatchers.IO) {
        dao.getAllFavorites().map {
            Pokemon(
                id = it.id,
                name = it.name,
                imageUrl = it.imageUrl,
                captureLocation = it.captureLocation
            )
        }
    }

    suspend fun isFavorite(id: Int): Boolean = withContext(Dispatchers.IO) {
        dao.isFavorite(id)
    }

    suspend fun removeFavorite(id: Int) = withContext(Dispatchers.IO) {
        dao.removeFavorite(id)
    }
}
