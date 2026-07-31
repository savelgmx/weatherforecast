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

        return getCurrentLocationOrNull() ?: emptyArray()
    }

    private suspend fun getCurrentLocationOrNull(): Array<String?>? = withTimeoutOrNull(10_000) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            val task = try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            } catch (e: SecurityException) {
                Log.e("DefineDeviceLocation", "getCurrentLocation SecurityException: ${e.message}")
                null
            }
            if (task == null) {
                // SecurityException path — fall back to last known location
                if (continuation.isActive) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { last ->
                            if (continuation.isActive) {
                                cancellationTokenSource.cancel()
                                continuation.resume(if (last != null) showLocation(last) else emptyArray())
                            }
                        }
                        .addOnFailureListener {
                            if (continuation.isActive) {
                                cancellationTokenSource.cancel()
                                continuation.resume(emptyArray())
                            }
                        }
                }
            } else {
                task.addOnSuccessListener { loc ->
                    if (continuation.isActive) {
                        cancellationTokenSource.cancel()
                        continuation.resume(if (loc != null) showLocation(loc) else emptyArray())
                    }
                }.addOnFailureListener { e ->
                    Log.e("DefineDeviceLocation", "getCurrentLocation failed: ${e.message}")
                    if (continuation.isActive) {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { last ->
                                if (continuation.isActive) {
                                    cancellationTokenSource.cancel()
                                    continuation.resume(if (last != null) showLocation(last) else emptyArray())
                                }
                            }
                            .addOnFailureListener {
                                if (continuation.isActive) {
                                    cancellationTokenSource.cancel()
                                    continuation.resume(emptyArray())
                                }
                            }
                    }
                }
            }
            continuation.invokeOnCancellation { cancellationTokenSource.cancel() }
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
