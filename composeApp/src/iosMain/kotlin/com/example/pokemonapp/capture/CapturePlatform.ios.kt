package com.example.pokemonapp.capture

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual object CapturePlatform {
    private val activeDelegates = mutableSetOf<LocationDelegate>()

    actual suspend fun getCurrentLocation(): CaptureCoordinates {
        val manager = CLLocationManager()

        return suspendCancellableCoroutine { continuation ->
            val delegate = LocationDelegate(
                onLocation = { location ->
                    manager.stopUpdatingLocation()
                    activeDelegates.remove(this)
                    continuation.resume(
                        CaptureCoordinates(
                            latitude = location.coordinate.latitude,
                            longitude = location.coordinate.longitude
                        )
                    )
                },
                onError = { error ->
                    manager.stopUpdatingLocation()
                    activeDelegates.remove(this)
                    continuation.resumeWithException(
                        IllegalStateException(error.localizedDescription)
                    )
                }
            )

            activeDelegates.add(delegate)
            manager.delegate = delegate
            manager.desiredAccuracy = platform.CoreLocation.kCLLocationAccuracyBest
            manager.requestLocation()

            continuation.invokeOnCancellation {
                manager.stopUpdatingLocation()
                activeDelegates.remove(delegate)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun savePhoto(bytes: ByteArray, pokemonId: Int): String {
        val documents = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: NSTemporaryDirectory()

        val directory = "$documents/captures"
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = directory,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )

        val path = "$directory/pokemon_${pokemonId}_${platform.Foundation.NSDate().timeIntervalSince1970}.jpg"
        bytes.usePinned { pinned ->
            NSData.dataWithBytes(
                bytes = pinned.addressOf(0),
                length = bytes.size.toULong()
            ).writeToFile(path, atomically = true)
        }
        return path
    }

    actual fun openMap(latitude: Double, longitude: Double) {
        val url = NSURL.URLWithString("http://maps.apple.com/?ll=$latitude,$longitude")
        if (url != null) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    private class LocationDelegate(
        private val onLocation: LocationDelegate.(CLLocation) -> Unit,
        private val onError: LocationDelegate.(NSError) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(
            manager: CLLocationManager,
            didUpdateLocations: List<*>
        ) {
            val location = didUpdateLocations.lastOrNull() as? CLLocation
            if (location != null) {
                onLocation(location)
            }
        }

        override fun locationManager(
            manager: CLLocationManager,
            didFailWithError: NSError
        ) {
            onError(didFailWithError)
        }
    }
}

