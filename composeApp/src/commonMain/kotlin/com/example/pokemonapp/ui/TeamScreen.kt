package com.example.pokemonapp.ui

import androidx.compose.runtime.Composable
import com.example.pokemonapp.data.Pokemon

@Composable
expect fun TeamScreenContent(
    team: List<Pokemon>,
    onRemovePokemon: (Pokemon) -> Unit
)
