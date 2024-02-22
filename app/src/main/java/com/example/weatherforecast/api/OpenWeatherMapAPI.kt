package com.example.weatherforecast.api



import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapAPI {
    //we will use openweather API
//https://api.openweathermap.org/data/2.5/weather?q=Krasnoyarsk&appid=33cc710b4ef18155198d89c3b2033f56&units=metric
//https://api.openweathermap.org/data/2.5/weather?appid=33cc710b4ef18155198d89c3b2033f56&units=metric&q=Krasnoyarsk&lang=ru
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("units") units: String,
        @Query("APPID") appId: String = AppConstants.API_KEY,
        @Query("lang") lang:String
    ): Response<WeatherResponse>

//for Forecast Weather we must use
//https://api.openweathermap.org/data/2.5/onecall?appid=33cc710b4ef18155198d89c3b2033f56&lon=92.791&lat=56.0097&units=metric&lang=ru

    @GET("onecall")
    suspend fun getForecastWeather(
        @Query("APPID") appId: String = AppConstants.API_KEY,
        @Query("lon") lon:String,
        @Query("lat")  lat:String,
        @Query("units") units: String,
        @Query("lang") lang:String
    ): Response<ForecastResponse>
}

