package com.example.weatherforecast.domain.repositories
// data/repositories/WeatherRepositoryImpl.kt



import com.example.weatherforecast.data.mappers.WeatherMapper
import com.example.weatherforecast.di.NetworkObject
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor() : WeatherRepository {
    private val apiService = NetworkObject.getAPIInstance()
    private val apiKey = "FS67QRY9G8HVLCANZJM6QBJJC" // Вынести в BuildConfig

    override fun getCurrentWeather(location: String): Flow<Resource<DailyWeather>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getWeather(location = location, apiKey = apiKey, include = "days")
            val currentWeather = WeatherMapper.toDailyWeather(response.days.first())
            emit(Resource.Success(currentWeather))
        } catch (e: IOException) {
            emit(Resource.Internet())
        } catch (e: HttpException) {
            emit(Resource.Error(msg = e.message()))
        } catch (e: Exception) {
            emit(Resource.Error(msg = e.message ?: "Unknown error"))
        }
    }

    override fun getForecast(location: String, includeHours: Boolean): Flow<Resource<List<DailyWeather>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getWeather(
                location = location,
                apiKey = apiKey,
                include = if (includeHours) "days,hours" else "days"
            )
            val forecast = response.days.map { WeatherMapper.toDailyWeather(it) }
            emit(Resource.Success(forecast))
        } catch (e: IOException) {
            emit(Resource.Internet())
        } catch (e: HttpException) {
            emit(Resource.Error(msg = e.message()))
        } catch (e: Exception) {
            emit(Resource.Error(msg = e.message ?: "Unknown error"))
        }
    }
}