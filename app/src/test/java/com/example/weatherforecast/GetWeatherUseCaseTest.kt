package com.example.weatherforecast

import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private val repository: VisualCrossingRepository = mockk()
    private lateinit var useCase: GetWeatherUseCase

    @Before
    fun setup() {
        useCase = GetWeatherUseCase(repository)
    }

    @Test
    fun getCurrentWeather_returnsSuccess() = runTest {
        val city = "London"
        val expected = DailyWeather(
            dew = 5.0,
            uvindex = 3,
            date = "2025-04-03",
            dt = 1708754400L,
            temp = 15.0,
            feelsLike = 13.0,
            tempMin = 10.0,
            tempMax = 20.0,
            pressure = 1013.0,
            visibility = 10000.0,
            humidity = 60,
            windSpeed = 4.5,
            windDeg = 200,
            cloudiness = 50,
            description = "Partly cloudy",
            icon = "02d",
            sunrise = 1708736117L,
            sunset = 1708772966L,
            moonPhase = 0.5,
            hours = null,
            timezone = "Europe/London",
            latitude = 51.5,
            longitude = -0.13
        )

        coEvery { repository.getCurrentWeather(city, false) } returns Resource.Success(expected)

        val result = useCase.getCurrentWeather(city)

        assertTrue(result is Resource.Success)
        assertEquals(expected.temp, (result as Resource.Success).data?.temp)
        assertEquals("Partly cloudy", result.data?.description)
    }

    @Test
    fun getCurrentWeather_returnsError() = runTest {
        val city = "InvalidCity"
        coEvery { repository.getCurrentWeather(city, false) } returns Resource.Error(msg = "City not found")

        val result = useCase.getCurrentWeather(city)

        assertTrue(result is Resource.Error)
        assertEquals("City not found", (result as Resource.Error).msg)
    }

    @Test
    fun getCurrentWeather_returnsLoading() = runTest {
        val city = "London"
        coEvery { repository.getCurrentWeather(city, false) } returns Resource.Loading()

        val result = useCase.getCurrentWeather(city)

        assertTrue(result is Resource.Loading)
    }

    @Test
    fun getForecastWeather_returnsSuccess() = runTest {
        val city = "London"
        val daily = DailyWeather(
            dew = 5.0,
            uvindex = 3,
            date = "2025-04-03",
            dt = 1708754400L,
            temp = 15.0,
            feelsLike = 13.0,
            tempMin = 10.0,
            tempMax = 20.0,
            pressure = 1013.0,
            visibility = 10000.0,
            humidity = 60,
            windSpeed = 4.5,
            windDeg = 200,
            cloudiness = 50,
            description = "Sunny",
            icon = "01d",
            sunrise = 1708736117L,
            sunset = 1708772966L,
            moonPhase = 0.5,
            hours = null,
            timezone = "Europe/London",
            latitude = 51.5,
            longitude = -0.13
        )
        val forecastList = listOf(daily)

        coEvery { repository.getForecastWeather(city, false) } returns Resource.Success(forecastList)

        val result = useCase.getForecastWeather(city)

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("Sunny", result.data?.first()?.description)
    }

    @Test
    fun getForecastWeather_withForceRefresh_forwardsToRepository() = runTest {
        val city = "London"
        coEvery { repository.getForecastWeather(city, true) } returns Resource.Loading()

        val result = useCase.getForecastWeather(city, forceRefresh = true)

        assertTrue(result is Resource.Loading)
    }
}
