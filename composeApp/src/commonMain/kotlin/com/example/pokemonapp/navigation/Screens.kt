package com.example.pokemonapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Pokedex : Screen
    
    @Serializable
    data class Details(val id: Int) : Screen
    
    @Serializable
    data object Team : Screen
}
