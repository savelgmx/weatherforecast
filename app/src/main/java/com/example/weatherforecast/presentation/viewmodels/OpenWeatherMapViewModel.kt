package com.example.weatherforecast.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.AppConstants
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
    private var currentCity:String = AppConstants.CITY_FORECAST

    init {
        getCurrentWeather(currentCity)
    }

    fun getCurrentWeather(city: String = currentCity, forceRefresh: Boolean = false) {
        if (!isWeatherLoaded || forceRefresh) {
            viewModelScope.launch {
                weatherLiveData.value = Resource.Loading()
                val result = getWeatherUseCase.getCurrentWeather(city)
                weatherLiveData.value = result
                if (result is Resource.Success) {
                    if (BuildConfig.DEBUG) Log.d("Map view model response", result.data.toString())
                    isWeatherLoaded = true
                    currentCity = city
                }
            }
        }
    }

    fun refreshWeather(city: String = currentCity) {
        isWeatherLoaded = false // Сбрасываем флаг для принудительного обновления
        getCurrentWeather(city, forceRefresh = true)
    }
}