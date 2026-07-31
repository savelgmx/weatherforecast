package com.example.weatherforecast.data.repositories
// =============================
// data/repositories/WeatherMapRepositoryImpl.kt
// =============================
import android.util.Log
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

// Repository implementation
class WeatherMapRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    @Named("visualCrossingApiKey") private val apiKey: String,
    @Named("weatherTileBaseUrl") private val tileBaseUrl: String,
    @Named("currentTime") private val currentTime: String
) : WeatherMapRepository {

    override suspend fun getWeatherPoints(city: String, layer: WeatherLayer): WeatherMapPointsResult {
        val response =
            api.getWeather(
                location = city,
                unitGroup = "metric",
                include = "days,hours",
                apiKey = apiKey,
                contentType = "json",
                lang = Locale.getDefault().language
            )

        // extract center
        val centerLat = response.latitude
        val centerLon = response.longitude

        // build points
        val points = mutableListOf<WeatherPoint>()
        val days = response.days ?: emptyList()
        if (days.isEmpty()) {
            return WeatherMapPointsResult(points = emptyList(), centerLat = centerLat, centerLon = centerLon)
        }

        days.forEach { day ->
            day.hours?.forEach { hour ->
                points.add(
                    WeatherPoint(
                        lat = centerLat ?: 0.0,
                        lon = centerLon ?: 0.0,
                        temperature = hour.temp,
                        precipitation = hour.precipitation ?: 0.0,
                        cloudCover = hour.cloudCover ?: 0.0
                    )
                )
            }
        }

        Log.d("WeatherMapRepo", "Returning ${points.size} points for $city (${centerLat ?: 0.0},${centerLon ?: 0.0})")
        return WeatherMapPointsResult(points = points, centerLat = centerLat, centerLon = centerLon)
    }

    override suspend fun getCityCenter(city: String): Pair<Double, Double>? {
        val response = api.getWeather(
            location = city,
            unitGroup = "metric",
            include = "days", // only need top-level fields
            apiKey = apiKey,
            contentType = "json",
            lang = Locale.getDefault().language
        )

        val lat = response.latitude
        val lon = response.longitude
        return if (lat != null && lon != null) {
            Pair(lat, lon)
        } else null
    }

    override fun getMapStyleUrl(): String {
        return "https://api.maptiler.com/maps/streets/style.json?key=o79YaVvsT94U5HX9WA6e"
    }

    override fun getWeatherTileUrl(layer: WeatherLayer): String {
        val path = layer.tilePath.lowercase(Locale.getDefault())
        return "$tileBaseUrl/$path/{z}/{x}/{y}.webp?apikey=$apiKey&time=latest&unitGroup=metric"
    }
}