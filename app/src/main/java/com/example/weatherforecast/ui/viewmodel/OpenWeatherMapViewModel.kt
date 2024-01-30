package com.example.weatherforecast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    fun getCurrentWeather(city: String) {
        viewModelScope.launch {
            _weatherLiveData.value = Resource.Loading()
            try {
                val result = repository.getWeatherForecast(city)
                _weatherLiveData.value = result
            } catch (e: Exception) {
                _weatherLiveData.value = Resource.Error(null, "An error occurred: ${e.message}")
            }
        }
    }
    }
