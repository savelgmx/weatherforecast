package com.example.weatherforecast.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.api.OpenWeatherMapRepository
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    private val repository: OpenWeatherMapRepository
) : ViewModel() {

    val weatherLiveData: MutableState<Resource<WeatherResponse>> = mutableStateOf(Resource.Loading())
    private var isWeatherLoaded = false // Flag to track if weather data is already loaded

    init {
        // Call this function when ViewModel is initialized
        getCurrentWeather() // or provide a default city
        isWeatherLoaded=true
    }

    fun getCurrentWeather() {
        if (!isWeatherLoaded) { // Check if weather data is already loaded
            viewModelScope.launch {
                weatherLiveData.value = Resource.Loading()
                try {
                    val result = repository.getCurrentWeather()

                    Log.d("Map view model response",result.toString())
                    weatherLiveData.value = result
                    isWeatherLoaded = true // Set flag to true after successful loading
                } catch (e: Exception) {
                    weatherLiveData.value = Resource.Error(null, "An error occurred: ${e.message}")
                }
            }
        }
    }
}
