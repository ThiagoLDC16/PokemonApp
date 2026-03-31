package com.example.pokemonapp.ui

import androidx.lifecycle.ViewModel
import com.example.pokemonapp.data.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PokemonViewModel : ViewModel() {
    private val _team = MutableStateFlow<List<Pokemon>>(emptyList())
    val team: StateFlow<List<Pokemon>> = _team.asStateFlow()

    fun addToTeam(pokemon: Pokemon) {
        if (_team.value.size < 6 && !_team.value.any { it.id == pokemon.id }) {
            _team.value = _team.value + pokemon
        }
    }

    fun removeFromTeam(pokemon: Pokemon) {
        _team.value = _team.value.filter { it.id != pokemon.id }
    }
}
