package com.example.pokemonapp.ui

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
    if (team.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seu time está vazio!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(team) { pokemon ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(pokemon.name) },
                        supportingContent = { Text("#${pokemon.id.toString().padStart(3, '0')}") },
                        trailingContent = {
                            IconButton(onClick = { onRemovePokemon(pokemon) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover")
                            }
                        }
                    )
                }
            }
        }
    }
}
