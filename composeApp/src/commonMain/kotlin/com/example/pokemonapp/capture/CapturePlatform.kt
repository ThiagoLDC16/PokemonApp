package com.example.pokemonapp.capture

data class CaptureCoordinates(
    val latitude: Double,
    val longitude: Double
)

expect object CapturePlatform {
    suspend fun getCurrentLocation(): CaptureCoordinates
    suspend fun savePhoto(bytes: ByteArray, pokemonId: Int): String
    fun openMap(latitude: Double, longitude: Double)
}
