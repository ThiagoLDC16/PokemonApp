package com.example.pokemonapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.data.Pokemon
import com.example.pokemonapp.data.PokemonRepository
import com.example.pokemonapp.data.PokemonType

@Composable
fun DetailsScreen(
    pokemonId: Int,
    onAddToTeam: (Pokemon) -> Unit,
    onNavigateBack: () -> Unit
) {
    val pokemon = remember(pokemonId) { PokemonRepository.getPokemonById(pokemonId) }

    if (pokemon == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pokémon não encontrado")
        }
        return
    }

    val mainType = pokemon.types.firstOrNull() ?: "Normal"
    val themeColor = Color(PokemonType.getColor(mainType))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header with Color Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(themeColor, themeColor.copy(alpha = 0.6f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "#${pokemon.id.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Content Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                // Types
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pokemon.types.forEach { type ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(type) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color(PokemonType.getColor(type)).copy(alpha = 0.1f),
                                labelColor = Color(PokemonType.getColor(type))
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sobre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pokemon.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Atributos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                StatRow("HP", pokemon.hp, themeColor)
                StatRow("Attack", pokemon.attack, themeColor)
                StatRow("Defense", pokemon.defense, themeColor)
                StatRow("Speed", pokemon.speed, themeColor)

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onAddToTeam(pokemon) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text("Adicionar ao Time", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value.toString(), fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { value / 150f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
