package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named


// Repository implementation
class WeatherMapRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    @Named("visualCrossingApiKey") private val apiKey: String
) : WeatherMapRepository {

    override suspend fun getWeatherPoints(city: String, layer: WeatherLayer): List<WeatherPoint> {
        val response = api.getWeather(
            city,"metric",
            "days,hours" ,
                 BuildConfig.API_KEY,
            "json",
            Locale.getDefault().language
        )
        val points = mutableListOf<WeatherPoint>()

        // Fake coordinates grid around the city (for visualization purposes)
        // In production you would request grid data from API
        val baseLat = 52.0
        val baseLon = 5.0

        response.days.firstOrNull()?.hours?.forEachIndexed { index, hour ->
            val lat = baseLat + (index % 5) * 0.1
            val lon = baseLon + (index / 5) * 0.1
            points.add(
                WeatherPoint(
                    lat = lat,
                    lon = lon,
                    temperature = hour.temp,
                    precipitation = 0.0, //hour.precip,
                    cloudCover = 0.0//hour.cloudcover
                )
            )
        }
        return points
    }
}
