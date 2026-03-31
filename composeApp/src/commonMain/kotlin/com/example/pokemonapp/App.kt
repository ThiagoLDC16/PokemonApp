package com.example.pokemonapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pokemonapp.navigation.Screen
import com.example.pokemonapp.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val viewModel: PokemonViewModel = viewModel { PokemonViewModel() }
    val team by viewModel.team.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val title = when {
        currentDestination?.hasRoute<Screen.Home>() == true -> "Home"
        currentDestination?.hasRoute<Screen.Pokedex>() == true -> "Pokédex"
        currentDestination?.hasRoute<Screen.Details>() == true -> "Detalhes"
        currentDestination?.hasRoute<Screen.Team>() == true -> "Meu Time"
        else -> "Pokédex App"
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (currentDestination?.hasRoute<Screen.Home>() == false) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Home>() } == true,
                        onClick = { navController.navigate(Screen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Pokedex>() } == true,
                        onClick = { navController.navigate(Screen.Pokedex) },
                        icon = { Icon(Icons.Default.List, contentDescription = "Pokédex") },
                        label = { Text("Pokédex") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Team>() } == true,
                        onClick = { navController.navigate(Screen.Team) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Time") },
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
                        onNavigateToPokedex = { navController.navigate(Screen.Pokedex) },
                        onNavigateToTeam = { navController.navigate(Screen.Team) }
                    )
                }
                composable<Screen.Pokedex> {
                    PokedexScreen(
                        onPokemonClick = { id -> navController.navigate(Screen.Details(id)) }
                    )
                }
                composable<Screen.Details> { backStackEntry ->
                    val details: Screen.Details = backStackEntry.toRoute()
                    DetailsScreen(
                        pokemonId = details.id,
                        onAddToTeam = { pokemon -> 
                            viewModel.addToTeam(pokemon)
                            navController.navigate(Screen.Team)
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.Team> {
                    TeamScreenContent(
                        team = team,
                        onRemovePokemon = { viewModel.removeFromTeam(it) }
                    )
                }
            }
        }
    }
}
