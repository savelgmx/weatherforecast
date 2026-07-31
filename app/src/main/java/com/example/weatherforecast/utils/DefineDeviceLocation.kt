package com.example.weatherforecast.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

class DefineDeviceLocation(private val context: Context) {

    suspend fun getLocation(): Array<String?> {
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return emptyArray()
        }

        val location = getCurrentLocationOrNull() ?: return emptyArray()
        return showLocation(location)
    }

    private suspend fun getCurrentLocationOrNull(): Location? = withTimeoutOrNull(10_000) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            continuation.invokeOnCancellation { cancellationTokenSource.cancel() }
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    if (continuation.isActive) continuation.resume(location)
                }
                .addOnFailureListener {
                    if (continuation.isActive) continuation.resume(null)
                }
        }
    }

    private fun showLocation(location: Location): Array<String?> {
        val geocoder = Geocoder(context, Locale.getDefault())
    var addresses: List<Address>? = null
    try {
        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    } catch (e: IOException) {
        Log.e("Geocoder", "Error: ${e.message}")
    }
        val cityName = addresses?.get(0)?.locality

        return arrayOf(location.latitude.toString(), location.longitude.toString(), cityName)
    }
}
