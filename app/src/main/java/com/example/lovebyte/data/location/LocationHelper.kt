package com.example.lovebyte.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val cityName: String?
)

class LocationHelper(private val context: Context) {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationResult(): LocationResult? {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        val lastKnown = suspendCancellableCoroutine<android.location.Location?> { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }

        val location = lastKnown ?: suspendCancellableCoroutine<android.location.Location?> { cont ->
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        } ?: return null

        val city = try {
            val geocoder = Geocoder(context, Locale.getDefault())
            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()
                ?.locality
        } catch (e: Exception) {
            null
        }

        return LocationResult(
            latitude = location.latitude,
            longitude = location.longitude,
            cityName = city
        )
    }
}