package com.example.pokemonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.pokemonapp.capture.CapturePlatform
import com.example.pokemonapp.data.local.getDatabaseBuilder
import com.example.pokemonapp.data.local.getRoomDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        CapturePlatform.initialize(this)

        setContent {
            val database = remember {
                val builder = getDatabaseBuilder(this@MainActivity)
                getRoomDatabase(builder)
            }
            App(database)
        }
    }
}
