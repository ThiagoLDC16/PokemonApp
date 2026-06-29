package com.example.pokemonapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pokemonapp.capture.CapturePlatform
import com.example.pokemonapp.data.Pokemon

@Composable
actual fun TeamScreenContent(
    team: List<Pokemon>,
    onRemovePokemon: (Pokemon) -> Unit
) {
    if (team.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seu time esta vazio!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(team) { pokemon ->
                TeamPokemonCard(
                    pokemon = pokemon,
                    onRemovePokemon = { onRemovePokemon(pokemon) }
                )
            }
        }
    }
}

@Composable
private fun TeamPokemonCard(
    pokemon: Pokemon,
    onRemovePokemon: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            pokemon.photoPath?.let { path ->
                AsyncImage(
                    model = path,
                    contentDescription = "Foto de captura de ${pokemon.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            ListItem(
                headlineContent = { Text(pokemon.name) },
                supportingContent = {
                    Column {
                        Text("#${pokemon.id.toString().padStart(3, '0')}")
                        CoordinatesText(pokemon)
                    }
                },
                trailingContent = {
                    IconButton(onClick = onRemovePokemon) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover")
                    }
                }
            )

            if (pokemon.latitude != null && pokemon.longitude != null) {
                OutlinedButton(
                    onClick = { CapturePlatform.openMap(pokemon.latitude, pokemon.longitude) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir no mapa")
                }
            }
        }
    }
}

@Composable
private fun CoordinatesText(pokemon: Pokemon) {
    val latitude = pokemon.latitude
    val longitude = pokemon.longitude
    if (latitude != null && longitude != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Lat: ${latitude.formatCoordinate()}")
            Text("Lng: ${longitude.formatCoordinate()}")
        }
    }
}

private fun Double.formatCoordinate(): String = toString().take(10)

