package com.example.pokemonapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemonapp.capture.CapturePlatform
import com.example.pokemonapp.data.Pokemon
import com.example.pokemonapp.data.PokemonType
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.location.LOCATION
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    pokemonId: Int,
    viewModel: PokemonViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.detailsState.collectAsStateWithLifecycle()
    val captureState by viewModel.captureState.collectAsStateWithLifecycle()
    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController = remember(permissionsFactory) {
        permissionsFactory.createPermissionsController()
    }
    val scope = rememberCoroutineScope()
    var showCamera by remember { mutableStateOf(false) }
    var captureError by remember { mutableStateOf<String?>(null) }

    BindEffect(permissionsController)

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetails(pokemonId)
        viewModel.resetCaptureState()
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
                        Text(
                            "Sobre",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                        Text(pokemon.description, modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            "Atributos Base",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                        StatRow("HP", pokemon.hp, themeColor)
                        StatRow("Attack", pokemon.attack, themeColor)
                        StatRow("Defense", pokemon.defense, themeColor)
                        StatRow("Speed", pokemon.speed, themeColor)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    captureError = null
                                    viewModel.resetCaptureState()
                                    try {
                                        permissionsController.providePermission(Permission.CAMERA)
                                        permissionsController.providePermission(Permission.LOCATION)
                                        showCamera = true
                                    } catch (deniedAlways: DeniedAlwaysException) {
                                        captureError = "Permissão negada permanentemente. Ative camera e localização nos ajustes do sistema."
                                    } catch (denied: DeniedException) {
                                        captureError = "Permissão de camera/localização negada."
                                    } catch (error: Throwable) {
                                        captureError = error.message ?: "Não foi possível solicitar permissões."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capturar para o Time", fontWeight = FontWeight.Bold)
                        }

                        captureError?.let { message ->
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        when (val capture = captureState) {
                            CaptureUiState.Idle -> Unit
                            CaptureUiState.Loading -> Text(
                                text = "Salvando foto e coordenadas...",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            CaptureUiState.Success -> Text(
                                text = "Pokemon salvo no time com foto e coordenadas.",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            is CaptureUiState.Error -> Text(
                                text = capture.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }

            if (showCamera) {
                CaptureCameraOverlay(
                    pokemon = pokemon,
                    themeColor = themeColor,
                    isSaving = captureState is CaptureUiState.Loading,
                    onClose = { showCamera = false },
                    onCaptured = { bytes ->
                        scope.launch {
                            viewModel.setCaptureLoading()
                            try {
                                val photoPath = CapturePlatform.savePhoto(bytes, pokemon.id)
                                val coordinates = CapturePlatform.getCurrentLocation()
                                viewModel.addToTeam(
                                    pokemon = pokemon,
                                    latitude = coordinates.latitude,
                                    longitude = coordinates.longitude,
                                    photoPath = photoPath
                                )
                                showCamera = false
                            } catch (error: Throwable) {
                                viewModel.setCaptureError(error.message ?: "Falha ao salvar captura.")
                            }
                        }
                    },
                    onError = { message ->
                        viewModel.setCaptureError(message)
                    }
                )
            }
        }
    }
}

@Composable
private fun CaptureCameraOverlay(
    pokemon: Pokemon,
    themeColor: Color,
    isSaving: Boolean,
    onClose: () -> Unit,
    onCaptured: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    val cameraState = rememberPeekabooCameraState(
        onCapture = { bytes ->
            if (bytes != null) {
                onCaptured(bytes)
            } else {
                onError("Nao foi possivel capturar a foto.")
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PeekabooCamera(
                state = cameraState,
                modifier = Modifier.fillMaxSize(),
                permissionDeniedContent = {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Permissão de camera negada.",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capturar ${pokemon.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, enabled = !isSaving) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar camera", tint = Color.White)
                }
            }

            FloatingActionButton(
                onClick = { cameraState.capture() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                containerColor = themeColor,
                contentColor = Color.White
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Tirar foto")
                }
            }
        }
    }
}

@Composable
fun PokemonDetailsHeader(pokemon: Pokemon) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                pokemon.name,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                "#${pokemon.id.toString().padStart(3, '0')}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
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
