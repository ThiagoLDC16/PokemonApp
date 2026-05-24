package com.example.pokemonapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.Pokemon
import com.example.pokemonapp.data.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    // Pokedex List State
    private val _pokedexState = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Loading)
    val pokedexState: StateFlow<UiState<List<Pokemon>>> = _pokedexState.asStateFlow()

    private var currentPokedexList = mutableListOf<Pokemon>()
    private var currentPage = 0
    private val pageSize = 20
    private var currentQuery = ""
    private var selectedType: String? = null
    private var isLastPage = false

    // Details State
    private val _detailsState = MutableStateFlow<UiState<Pokemon>>(UiState.Loading)
    val detailsState: StateFlow<UiState<Pokemon>> = _detailsState.asStateFlow()

    // Team/Favorites State
    private val _team = MutableStateFlow<List<Pokemon>>(emptyList())
    val team: StateFlow<List<Pokemon>> = _team.asStateFlow()

    init {
        loadInitialData()
        loadTeam()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                repository.syncIfNecessary()
                refreshPokedex()
            } catch (e: Exception) {
                _pokedexState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun searchPokemon(query: String) {
        currentQuery = query
        refreshPokedex()
    }

    fun filterByType(type: String?) {
        selectedType = type
        refreshPokedex()
    }

    private fun refreshPokedex() {
        currentPage = 0
        currentPokedexList.clear()
        isLastPage = false
        _pokedexState.value = UiState.Loading
        loadNextPokedexPage()
    }

    fun loadNextPokedexPage() {
        if (isLastPage) return

        viewModelScope.launch {
            try {
                val newItems = repository.getPagedPokemon(currentQuery, selectedType, currentPage, pageSize)
                if (newItems.isEmpty() && currentPage == 0) {
                     _pokedexState.value = UiState.Success(emptyList())
                } else if (newItems.isEmpty()) {
                    isLastPage = true
                } else {
                    currentPokedexList.addAll(newItems)
                    _pokedexState.value = UiState.Success(currentPokedexList.toList())
                    currentPage++
                }
            } catch (e: Exception) {
                _pokedexState.value = UiState.Error(e.message ?: "Erro ao carregar lista")
            }
        }
    }

    fun loadPokemonDetails(id: Int) {
        _detailsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val details = repository.getPokemonDetails(id)
                _detailsState.value = UiState.Success(details)
            } catch (e: Exception) {
                _detailsState.value = UiState.Error(e.message ?: "Erro ao carregar detalhes")
            }
        }
    }

    private fun loadTeam() {
        viewModelScope.launch {
            _team.value = repository.getFavorites()
        }
    }

    fun addToTeam(pokemon: Pokemon, location: String) {
        viewModelScope.launch {
            repository.saveFavorite(pokemon, location)
            loadTeam()
        }
    }

    fun removeFromTeam(id: Int) {
        viewModelScope.launch {
            repository.removeFavorite(id)
            loadTeam()
        }
    }
}
