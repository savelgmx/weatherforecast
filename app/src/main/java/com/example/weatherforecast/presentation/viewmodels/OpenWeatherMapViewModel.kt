package com.example.weatherforecast.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    val weatherLiveData: MutableState<Resource<WeatherResponse>> = mutableStateOf(Resource.Loading())
    private var isWeatherLoaded = false // Flag to track if weather data is already loaded

    init {
        getCurrentWeather()
        isWeatherLoaded = true // As per OpenWeatherForecastViewModel logic
    }

    fun getCurrentWeather() {
        if (!isWeatherLoaded) {
            viewModelScope.launch {
                weatherLiveData.value = Resource.Loading()
                val result = getWeatherUseCase.getCurrentWeather()
                weatherLiveData.value = result
                if (result is Resource.Success) {
                    Log.d("Map view model response", result.data.toString())
                    isWeatherLoaded = true // Set only on success
                }
            }
        }
    }
}
