package com.example.weatherforecast.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val sharedPreferences: SharedPreferences) : ViewModel() {
    val temperatureUnitsLiveData = MutableLiveData<Boolean>()
    val distanceUnitsLiveData = MutableLiveData<String>()

    init {
        // Initialize with current preferences
        temperatureUnitsLiveData.value = sharedPreferences.getBoolean("temperature_units", true)
        distanceUnitsLiveData.value = sharedPreferences.getString("distance_units", "metric")
}

    fun setTemperatureUnits(isCelsius: Boolean) {
        temperatureUnitsLiveData.value = isCelsius
        sharedPreferences.edit().putBoolean("temperature_units", isCelsius).apply()
    }

    fun setDistanceUnits(units: String) {
        distanceUnitsLiveData.value = units
        sharedPreferences.edit().putString("distance_units", units).apply()
    }
}
