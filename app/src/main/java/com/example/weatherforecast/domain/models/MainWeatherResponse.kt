package com.example.weatherforecast.domain.models

data class MainWeatherResponse(

    val daily: List<DailyForecast>,
    val hourly: List<HourlyForecast>


) {
    class DailyForecast {

    }
    //
/*
    val cityName = currentState.data?.name
    val humidity= currentState.data?.main?.humidity
    val dewPoint = forecastState.data?.current?.dewPoint
    val windSpeed = currentState.data?.wind?.speed?.toInt()
    val windDegree= currentState.data?.wind?.deg
    val timeOfSunrise = forecastState.data?.current?.sunrise.let { WeatherUtils.updateTime(it) }
    val timeOfSunset = forecastState.data?.current?.sunset.let{ WeatherUtils.updateTime(it)}

    val timeOfDawn=forecastState.data?.current?.sunrise
    val timeOfDusk=forecastState.data?.current?.sunset
    val timeOfDawnAndDusk= WeatherUtils.calculateDawnAndDusk(timeOfDawn,timeOfDusk)//it returns array of two elements

    val uvIndex = forecastState.data?.current?.uvi
    val pressureValue = currentState.data?.main?.pressure

    val moonPhase = forecastState.data?.daily?.get(0)?.moonPhase
    val moonRise = forecastState.data?.daily?.get(0)?.moonrise.let { WeatherUtils.updateTime(it) }
    val moonSet = forecastState.data?.daily?.get(0)?.moonset.let { WeatherUtils.updateTime(it) }
*/


}

class HourlyForecast {

}
