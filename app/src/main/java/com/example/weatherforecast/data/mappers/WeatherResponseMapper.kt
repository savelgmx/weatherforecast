package com.example.weatherforecast.data.mappers

import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Current
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Temp
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
//преобразует доменные модели в старые модели для UI.

object WeatherResponseMapper {
    fun toWeatherResponse(dailyWeather: DailyWeather, cityName: String): WeatherResponse {
        return WeatherResponse(
            coord = Coord(0.0, 0.0),
            weather = listOf(Weather(0, dailyWeather.description, dailyWeather.description, dailyWeather.icon)),
            base = "stations",
            main = Main(
                temp = dailyWeather.temp.toDouble(),
                feels_like = dailyWeather.feelsLike,
                temp_min = dailyWeather.tempMin,
                temp_max = dailyWeather.tempMax,
                pressure = dailyWeather.pressure.toInt(),
                humidity = dailyWeather.humidity
            ),
            visibility = 10000,
            wind = Wind(dailyWeather.windSpeed, dailyWeather.windDeg),
            clouds = Clouds(dailyWeather.cloudiness),
            dt = dailyWeather.dt ,
            sys = Sys(0, 0, "N/A", dailyWeather.sunrise, dailyWeather.sunset),
            timezone = 0,
            id = 0,
            name = cityName,  // Используйте переданный cityName
            cod = 200
        )
    }

    fun toForecastResponse(dailyWeathers: List<DailyWeather>): ForecastResponse {
        val forecastItems = dailyWeathers.map { daily ->
            Daily(
                clouds = daily.cloudiness,
                dewPoint = 0.0,
                dt = daily.dt.toInt(),
                feelsLike = FeelsLike(daily.feelsLike, daily.feelsLike, daily.feelsLike, daily.feelsLike),
                moonPhase = daily.moonPhase,
                humidity = daily.humidity,
                sunrise = daily.sunrise.toInt(),
                sunset = daily.sunset.toInt(),
                pressure = daily.pressure.toInt(),
                moonrise = 0,
                moonset = 0,
                temp = Temp(daily.tempMin, daily.tempMax, daily.tempMin, daily.tempMax, daily.temp, daily.temp),
                uvi = 0.0,
                weather = listOf(Weather(0, daily.description, daily.description, daily.icon)),
                windDeg = daily.windDeg,
                windGust = 0.0,
                windSpeed = daily.windSpeed
            )
        }

        // Извлекаем почасовые данные
        val allHourly = dailyWeathers.flatMap { it.hours ?: emptyList() }.map { hourlyWeather ->
            Hourly(
                dt = hourlyWeather.dt.toInt(),
                temp = hourlyWeather.temp,
                feelsLike = hourlyWeather.feelsLike,
                pressure = hourlyWeather.pressure.toInt(),
                humidity = hourlyWeather.humidity,
                dewPoint = 0.0, // Добавьте реальное значение, если доступно
                uvi = 0.0,
                clouds = hourlyWeather.cloudiness,
                visibility = 10000,
                windSpeed = hourlyWeather.windSpeed,
                windDeg = hourlyWeather.windDeg,
                windGust = 0.0,
                weather = listOf(Weather(0, hourlyWeather.description, hourlyWeather.description, hourlyWeather.icon))
            )
        }

        val moonPhase = if (dailyWeathers.isNotEmpty()) dailyWeathers.first().moonPhase else 0.0
        return ForecastResponse(
            current = Current(0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0, emptyList(), 0, 0.0, 0.0),
            daily = forecastItems,
            hourly = allHourly,
            lat = 0.0,
            lon = 0.0,
            timezone = "",
            timezoneOffset = 0
        )
    }
}