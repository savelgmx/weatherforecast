package com.example.weatherforecast.data.repositories
// =============================
// data/repositories/WeatherMapRepositoryImpl.kt
// =============================
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer
import java.util.Locale
import javax.inject.Inject


// Repository implementation
class WeatherMapRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
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

        // coordinates grid around the city (for visualization purposes)
        // request grid data from API
        val baseLat = response.latitude //52.0
        val baseLon = response.longitude //5.0

        response.days.firstOrNull()?.hours?.forEachIndexed { index, hour ->
            val lat = baseLat + (index % 5) * 0.1
            val lon = baseLon + (index / 5) * 0.1
            points.add(
                WeatherPoint(
                    lat = lat,
                    lon = lon,
                    temperature = hour.temp,
                    precipitation = hour.precipitation, //hour.precip,
                    cloudCover = hour.cloudCover//hour.cloudcover
                )
            )
        }
        return points
    }
}
