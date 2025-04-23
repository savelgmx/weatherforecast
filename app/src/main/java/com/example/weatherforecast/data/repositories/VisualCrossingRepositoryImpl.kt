package com.example.weatherforecast.data.repositories


import androidx.lifecycle.LiveData
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.db.CurrentWeatherEntity
import com.example.weatherforecast.data.mappers.WeatherMapper
import com.example.weatherforecast.data.mappers.WeatherResponseMapper
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.di.ContextProvider
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class VisualCrossingRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    contextProvider: ContextProvider
) : VisualCrossingRepository {


    override suspend fun getCurrentWeather(): Resource<WeatherResponse> {
        return try {
            val response = apiService.getWeather(
                location = "hamburg",
                apiKey = BuildConfig.API_KEY,
                include = "days"
            )
            val dailyWeather = WeatherMapper.toDailyWeather(response.days.first())
            val weatherResponse = WeatherResponseMapper.toWeatherResponse(dailyWeather)
            Resource.Success(weatherResponse)
        } catch (e: IOException) {
            Resource.Internet()
        } catch (e: HttpException) {
            Resource.Error(msg = "API error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error(msg = "Unknown error: ${e.message}")
        }
    }

    override suspend fun getForecastWeather(): Resource<ForecastResponse> {
        return try {
            val response = apiService.getWeather(
                location = "hamburg",
                apiKey = BuildConfig.API_KEY,
                include = "days,hours"
            )
            val dailyWeathers = response.days.map { WeatherMapper.toDailyWeather(it) }
            val forecastResponse = WeatherResponseMapper.toForecastResponse(dailyWeathers)
            Resource.Success(forecastResponse)
        } catch (e: IOException) {
            Resource.Internet()
        } catch (e: HttpException) {
            Resource.Error(msg = "API error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error(msg = "Unknown error: ${e.message}")
        }
    }

    override suspend fun getWeatherForecastFromDB(): LiveData<List<CurrentWeatherEntity>> {
        throw NotImplementedError("Database implementation not provided")
    }
}