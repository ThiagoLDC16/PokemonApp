package com.example.pokemonapp

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.example.pokemonapp.data.local.getDatabaseBuilder
import com.example.pokemonapp.data.local.getRoomDatabase

fun MainViewController() = ComposeUIViewController {
    val database = remember {
        val builder = getDatabaseBuilder()
        getRoomDatabase(builder)
    }
    App(database)
}
