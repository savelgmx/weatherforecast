package com.example.weatherforecast.presentation.viewmodels
// presentation/viewmodels/WeatherViewModel.kt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _currentWeather = MutableLiveData<Resource<DailyWeather>>()
    val currentWeather: LiveData<Resource<DailyWeather>> = _currentWeather

    private val _forecast = MutableLiveData<Resource<List<DailyWeather>>>()
    val forecast: LiveData<Resource<List<DailyWeather>>> = _forecast

    fun fetchWeather(location: String, includeHours: Boolean = false) {
        viewModelScope.launch {
            getWeatherUseCase.getCurrentWeather(location).collect { resource ->
                _currentWeather.postValue(resource)
            }
            getWeatherUseCase.getForecast(location, includeHours).collect { resource ->
                _forecast.postValue(resource)
            }
        }
    }
}