package com.example.weatherforecast.presentation.viewmodels
// presentation/viewmodels/WeatherViewModel.kt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _currentWeather = MutableLiveData<Resource<WeatherResponse>>()
    val currentWeather: LiveData<Resource<WeatherResponse>> = _currentWeather

    private val _forecast = MutableLiveData<Resource<ForecastResponse>>()
    val forecast: LiveData<Resource<ForecastResponse>> = _forecast

    fun fetchWeather() {
        viewModelScope.launch {
            _currentWeather.postValue(getWeatherUseCase.getCurrentWeather())
            _forecast.postValue(getWeatherUseCase.getForecastWeather())
        }
    }
}