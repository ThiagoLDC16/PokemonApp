package com.example.pokemonapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.Pokemon

@Composable
actual fun TeamScreenContent(
    team: List<Pokemon>,
    onRemovePokemon: (Pokemon) -> Unit
) {
    // No iOS, vamos usar uma estética mais limpa, típica de listas do iOS
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Meu Time",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
        
        if (team.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum Pokémon no seu time.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(team) { pokemon ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(pokemon.name, style = MaterialTheme.typography.titleMedium)
                                Text("#${pokemon.id}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onRemovePokemon(pokemon) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remover",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}
