package com.example.weatherforecast.utils

import android.content.Context
import com.example.weatherforecast.components.DataStoreManager
import kotlinx.coroutines.flow.first

object CityResolver {

    /**
     * Gets the city name from DataStore preferences or device location
     * @param context Application context
     * @return City name or empty string if not available
     */
    suspend fun getCityName(context: Context): String {
        // First check DataStore for saved city
        val savedCity = DataStoreManager.cityNamePrefFlow(context).first()
        if (!savedCity.isNullOrBlank()) {
            return savedCity
        }

        // If no saved city, try device location
        val defineLocation = DefineDeviceLocation(context)
        val locationArray = defineLocation.getLocation()
        if (locationArray.isNotEmpty() && locationArray.size == 3) {
            return locationArray[2] ?: ""
        }

        return ""
    }

    /**
     * Saves city name to DataStore preferences
     * @param context Application context
     * @param cityName City name to save
     */
    suspend fun saveCityName(context: Context, cityName: String) {
        DataStoreManager.updateCityName(context, cityName)
    }
}
