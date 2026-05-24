package com.example.pokemonapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pokemonapp.data.PokemonRepository
import com.example.pokemonapp.data.local.AppDatabase
import com.example.pokemonapp.navigation.Screen
import com.example.pokemonapp.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(database: AppDatabase) {
    val navController = rememberNavController()
    
    val repository = remember { PokemonRepository(database) }
    val viewModel: PokemonViewModel = viewModel { PokemonViewModel(repository) }
    
    val team by viewModel.team.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val title = when {
        currentDestination?.hasRoute<Screen.Home>() == true -> "Pokédex Home"
        currentDestination?.hasRoute<Screen.Pokedex>() == true -> "Pokédex List"
        currentDestination?.hasRoute<Screen.Details>() == true -> "Detalhes"
        currentDestination?.hasRoute<Screen.Team>() == true -> "Meu Time"
        else -> "Pokédex"
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        ) 
                    },
                    navigationIcon = {
                        if (currentDestination?.hasRoute<Screen.Home>() == false) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Home>() } == true,
                        onClick = { 
                            if (currentDestination?.hasRoute<Screen.Home>() == false) {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Pokedex>() } == true,
                        onClick = { 
                            if (currentDestination?.hasRoute<Screen.Pokedex>() == false) {
                                navController.navigate(Screen.Pokedex) {
                                    popUpTo(Screen.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Pokédex") },
                        label = { Text("Pokédex") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Team>() } == true,
                        onClick = { 
                            if (currentDestination?.hasRoute<Screen.Team>() == false) {
                                navController.navigate(Screen.Team) {
                                    popUpTo(Screen.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Time") },
                        label = { Text("Time") }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<Screen.Home> {
                    HomeScreen(
                        onNavigateToPokedex = { 
                            navController.navigate(Screen.Pokedex) {
                                popUpTo(Screen.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToTeam = { 
                            navController.navigate(Screen.Team) {
                                popUpTo(Screen.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable<Screen.Pokedex> {
                    PokedexScreen(
                        viewModel = viewModel,
                        onPokemonClick = { id -> navController.navigate(Screen.Details(id)) }
                    )
                }
                composable<Screen.Details> { backStackEntry ->
                    val details: Screen.Details = backStackEntry.toRoute()
                    DetailsScreen(
                        pokemonId = details.id,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.Team> {
                    TeamScreenContent(
                        team = team,
                        onRemovePokemon = { viewModel.removeFromTeam(it.id) }
                    )
                }
            }
        }
    }
}
