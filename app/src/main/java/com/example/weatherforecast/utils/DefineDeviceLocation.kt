package com.example.weatherforecast.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import java.io.IOException
import java.util.Locale

class DefineDeviceLocation(private val context: Context) {

    fun getLocation(): Array<String?> {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return emptyArray()
        var locationArray = emptyArray<String?>()

        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return locationArray
        }

        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastKnownLocation != null) {
            locationArray = showLocation(lastKnownLocation)
        }

        return locationArray
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
