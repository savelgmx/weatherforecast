package com.example.weatherforecast.domain

import com.example.weatherforecast.domain.model.MainWeatherResponse

object WeatherMapper {

    fun fromForecastResponse():MainWeatherResponse{


        return MainWeatherResponse()
    }


}