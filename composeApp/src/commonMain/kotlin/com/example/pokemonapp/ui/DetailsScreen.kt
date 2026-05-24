package com.example.pokemonapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.data.Pokemon
import com.example.pokemonapp.data.PokemonType
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetailsScreen(
    pokemonId: Int,
    viewModel: PokemonViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.detailsState.collectAsStateWithLifecycle()
    var captureLocation by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetails(pokemonId)
    }

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Erro: ${state.message}")
            }
        }
        is UiState.Success -> {
            val pokemon = state.data
            val themeColor = Color(PokemonType.getColor(pokemon.types.firstOrNull() ?: "Normal"))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(themeColor)
            ) {
                PokemonDetailsHeader(pokemon)
                
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CatchingPokemon,
                        contentDescription = null,
                        modifier = Modifier.size(140.dp),
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Sobre", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = themeColor)
                        Text(pokemon.description, modifier = Modifier.padding(vertical = 8.dp))

                        Text("Atributos Base", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = themeColor)
                        StatRow("HP", pokemon.hp, themeColor)
                        StatRow("Attack", pokemon.attack, themeColor)
                        StatRow("Defense", pokemon.defense, themeColor)
                        StatRow("Speed", pokemon.speed, themeColor)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                        ) {
                            Text("Favoritar / Adicionar ao Time", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Onde você capturou?") },
                    text = {
                        OutlinedTextField(
                            value = captureLocation,
                            onValueChange = { captureLocation = it },
                            label = { Text("Local de captura") },
                            placeholder = { Text("Ex: Pallet Town") }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (captureLocation.isNotBlank()) {
                                    viewModel.addToTeam(pokemon, captureLocation)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Salvar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PokemonDetailsHeader(pokemon: Pokemon) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(pokemon.name, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = Color.White)
            Text("#${pokemon.id.toString().padStart(3, '0')}", style = MaterialTheme.typography.titleLarge, color = Color.White.copy(alpha = 0.8f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            pokemon.types.forEach { type ->
                Surface(color = Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(20.dp)) {
                    Text(type, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: Int, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.labelLarge)
        Text(value.toString().padStart(3, '0'), modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier.weight(1f).height(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp)),
            color = color,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
