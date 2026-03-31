package com.example.pokemonapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform