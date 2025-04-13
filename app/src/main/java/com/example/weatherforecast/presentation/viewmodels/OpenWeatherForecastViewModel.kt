package com.example.weatherforecast.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.api.OpenWeatherMapRepository
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherForecastViewModel @Inject constructor(
    private val repository: OpenWeatherMapRepository
):ViewModel(){

    val forecastLiveData: MutableState<Resource<ForecastResponse>> = mutableStateOf(Resource.Loading())
    private var isForecastLoaded = false // Flag to track if forecast data is already loaded
    init {
        getForecastWeather()
        isForecastLoaded = true
    }
    fun getForecastWeather(){
        if(!isForecastLoaded){
            viewModelScope.launch {
                forecastLiveData.value = Resource.Loading()
                try {
                    val result = repository.getForecastWeather()
                    forecastLiveData.value = result
                    Log.d("ViewModel result response",result.toString())

                }catch (e:Exception){
                    forecastLiveData.value = Resource.Error(null, "An error occurred: ${e.message}")
                }
            }
        }
    }
}

