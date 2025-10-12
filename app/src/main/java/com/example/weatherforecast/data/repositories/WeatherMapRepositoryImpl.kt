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
import javax.inject.Named

data class WeatherMapResponseWithCenter(
    val centerLat: Double,
    val centerLon: Double,
    val points: List<WeatherPoint>
)

// Repository implementation
class WeatherMapRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    @Named("visualCrossingApiKey") private val apiKey: String,
    @Named("weatherTileBaseUrl") private val tileBaseUrl: String,
    @Named("currentTime") private val currentTime: String
) : WeatherMapRepository {

    private var lastCenter: Pair<Double, Double>? = null

    // styleUrl вынесён сюда — использует MAPTILER_API_KEY из BuildConfig
    override fun getMapStyleUrl(): String {
        // MapTiler style URL — ключ в BuildConfig (устанавливается через build.gradle)
        return "https://api.maptiler.com/maps/streets/style.json?key=${BuildConfig.MAPTILER_API_KEY}"
    }

    // Формируем шаблон тайл-URL для погодного слоя.
    // Важно: точный формат url для вашего tile-провайдера может отличаться;
    // используйте tileBaseUrl и необходимые параметры.
    override fun getWeatherTileUrl(layer: WeatherLayer): String {
        // Пример: https://weather.visualcrossing.com/.../{layer}/{z}/{x}/{y}.png?key=API&time=YYYY-MM-DD
        val layerPath = layer.name.lowercase(Locale.getDefault())
        return "${tileBaseUrl.trimEnd('/')}/$layerPath/{z}/{x}/{y}.png?key=${apiKey}&time=${currentTime}"
    }

    override suspend fun getWeatherPoints(city: String, layer: WeatherLayer): List<WeatherPoint> {
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
        val centerLat = response.latitude ?: 0.0
        val centerLon = response.longitude ?: 0.0
        lastCenter = Pair(centerLat, centerLon)

        // build points
        val points = mutableListOf<WeatherPoint>()
        val hours = response.days?.flatMap { it.hours ?: emptyList() } ?: emptyList()
        return hours.map { hour ->
            WeatherPoint(
                lat = centerLat,
                lon = centerLon,
                temperature = hour.temp,
                precipitation = hour.precipitation ?: 0.0,
                cloudCover = hour.cloudCover ?: 0.0
            )
        }
    }

    override suspend fun getCityCenter(city: String): Pair<Double, Double>? {
        lastCenter?.let { return it }

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
            Pair(lat, lon).also { lastCenter = it }
        } else null
    }
}
