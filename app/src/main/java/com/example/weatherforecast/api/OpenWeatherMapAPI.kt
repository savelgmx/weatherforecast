package com.example.weatherforecast.api


import com.example.weatherforecast.di.NetworkObject
import com.example.weatherforecast.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapAPI {

  @GET("weather")
  suspend fun getCurrentWeather(
    @Query("q") cityName: String,
    @Query("units") units: String = "metric",
    @Query("APPID") appId: String = NetworkObject.API_KEY,
  ): Response<WeatherResponse>

  @GET("weather")
  suspend fun getWeatherLatLng(
    @Query("lat") lat: Double,
    @Query("lon") lon: Double,
    @Query("units") units: String = "metric",
    @Query("APPID") appId: String = NetworkObject.API_KEY,
  ): Response<WeatherResponse>
}

