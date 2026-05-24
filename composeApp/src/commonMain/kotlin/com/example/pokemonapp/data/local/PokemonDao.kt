package com.example.pokemonapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonEntity>)

    @Query("""
        SELECT * FROM pokemon_cache 
        WHERE name LIKE :query 
        AND (:type IS NULL OR types LIKE :type) 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPagedPokemon(query: String, type: String?, limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT COUNT(*) FROM pokemon_cache")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM pokemon_cache WHERE types = '' OR types IS NULL")
    suspend fun getCountMissingTypes(): Int
    
    @Query("UPDATE pokemon_cache SET types = :types WHERE id = :id")
    suspend fun updateTypes(id: Int, types: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun removeFavorite(id: Int)
}
