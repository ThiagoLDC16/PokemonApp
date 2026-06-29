package com.example.pokemonapp.capture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

actual object CapturePlatform {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): CaptureCoordinates {
        ensureInitialized()
        val client = LocationServices.getFusedLocationProviderClient(appContext)
        val cancellationTokenSource = CancellationTokenSource()

        return suspendCancellableCoroutine { continuation ->
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        CaptureCoordinates(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    continuation.resumeWithException(IllegalStateException("Localização indisponivel."))
                }
            }.addOnFailureListener { error ->
                continuation.resumeWithException(error)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }

    actual suspend fun savePhoto(bytes: ByteArray, pokemonId: Int): String {
        ensureInitialized()
        val directory = File(appContext.filesDir, "captures")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "pokemon_${pokemonId}_${System.currentTimeMillis()}.jpg")
        file.writeBytes(bytes)
        return file.absolutePath
    }

    actual fun openMap(latitude: Double, longitude: Double) {
        ensureInitialized()
        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }

    private fun ensureInitialized() {
        check(::appContext.isInitialized) {
            "CapturePlatform.initialize(context) deve ser chamado antes de usar recursos nativos."
        }
    }
}

