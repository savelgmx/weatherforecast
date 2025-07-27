package com.example.weatherforecast


import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherMapViewModel
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.utils.Resource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Юнит-тесты для OpenWeatherMapViewModel.
 */
class OpenWeatherMapViewModelTest {

    private lateinit var viewModel: OpenWeatherMapViewModel
    private val getWeatherUseCase: GetWeatherUseCase = mock()
    private val getDeviceCityUseCase: GetDeviceCityUseCase = mock()
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OpenWeatherMapViewModel(getWeatherUseCase, getDeviceCityUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls getCurrentWeather with city from getDeviceCityUseCase`() = runTest {
        // Arrange
        val city = "Hamburg"
        val weatherResponse = WeatherResponse(name = city,id=0,
            base = "base", coord = Coord(9.99183,53.5537),
            weather = List<Weather(0,"main","Donnerwetter","rain")> ,
            main = Main(20.0,20.0,14.0,25.0,1024,50),
            clouds = Clouds(10),
            wind = Wind(15.0,230), visibility = 10000, cod = 1,dt=1753567200,
            sys = Sys(1,1"Deutschland",1753586854,1753644279),
            timezone =2
        )


        whenever(getDeviceCityUseCase.execute()).thenReturn(city)
        whenever(getWeatherUseCase.getCurrentWeather(city, false)).thenReturn(Resource.Success(weatherResponse))

        // Act
        viewModel // Инициализация ViewModel вызывает init

        // Assert
        assertTrue(viewModel.weatherLiveData.value is Resource.Success)
        assertEquals(city, (viewModel.weatherLiveData.value as Resource.Success).data?.name)
        verify(getWeatherUseCase).getCurrentWeather(city, false)
    }

    @Test
    fun `refreshWeather calls getCurrentWeather with city from getDeviceCityUseCase`() = runTest {
        // Arrange
        val city = "Hamburg"
        val weatherResponse = WeatherResponse(name = city,id=0,
            base = "base", coord = Coord(9.99183,53.5537),
            weather = List<Weather(0,"main","Donnerwetter","rain")> ,
            main = Main(20.0,20.0,14.0,25.0,1024,50),
            clouds = Clouds(10),
            wind = Wind(15.0,230), visibility = 10000, cod = 1,dt=1753567200,
            sys = Sys(1,1"Deutschland",1753586854,1753644279),
            timezone =2
        )
        whenever(getDeviceCityUseCase.execute()).thenReturn(city)
        whenever(getWeatherUseCase.getCurrentWeather(city, false)).thenReturn(Resource.Success(weatherResponse))
        whenever(getWeatherUseCase.getCurrentWeather(city, true)).thenReturn(Resource.Success(weatherResponse))

        // Act
        viewModel.refreshWeather()
        // Ждём завершения корутины
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(getWeatherUseCase).getCurrentWeather(city, true)
        assertTrue(viewModel.weatherLiveData.value is Resource.Success)
        assertEquals(city, (viewModel.weatherLiveData.value as Resource.Success).data?.name)
    }
}