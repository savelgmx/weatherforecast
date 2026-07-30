package com.example.weatherforecast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherforecast.components.DailyWeatherForecast
import com.example.weatherforecast.components.MainScreen
import com.example.weatherforecast.components.WeatherMapScreen
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherForecastViewModel
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherMapViewModel
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 123
    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // Inject both ViewModels so we can notify them when location permission is granted
    private val mapViewModel: OpenWeatherMapViewModel by viewModels()
    private val forecastViewModel: OpenWeatherForecastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherNavGraph()
                }
            }
        }
        requestLocationPermissionsIfNeeded()
    }

    @Composable
    private fun WeatherNavGraph() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "forecast") {
            composable("forecast") {
                val currentViewModel: OpenWeatherMapViewModel = hiltViewModel()
                val forecastViewModel: OpenWeatherForecastViewModel = hiltViewModel()
                val mapViewModel: WeatherMapViewModel = hiltViewModel()
                MainScreen(
                    navController = navController,
                    currentState = currentViewModel.weatherLiveData.value,
                    forecastState = forecastViewModel.forecastLiveData.value,
                    onRefresh = {
                        currentViewModel.refreshWeather()
                        forecastViewModel.refreshWeather()
                    },
                    cityName = currentViewModel.currentCity,
                    showCitySelectionDialog = currentViewModel.showCitySelectionDialog.value,
                    onCitySelected = { cityName ->
                        currentViewModel.onCitySelected(cityName)
                        forecastViewModel.selectCity(cityName)
                    },
                    onDismissCityDialog = {
                        currentViewModel.dismissCitySelectionDialog()
                    },
                    pollution = currentViewModel.airVisualLiveData.value?.data?.current?.pollution
                )
            }
            composable(
                route = "detail/{index}",
                arguments = listOf(navArgument("index") { type = NavType.IntType })
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("index") ?: 0
                val forecastViewModel: OpenWeatherForecastViewModel = hiltViewModel()
                val forecastState = forecastViewModel.forecastLiveData.value
                val dailyList = forecastState?.data ?: emptyList()
                val hourlyList = dailyList.flatMap { it.hours ?: emptyList() }
                if (dailyList.isNotEmpty() && index in 0 until dailyList.size) {
                    DailyWeatherForecast(
                        navController = navController,
                        dailyList = dailyList,
                        hourlyList = hourlyList,
                        startIndex = index,
                        timeZone = dailyList.firstOrNull()?.timezone ?: "UTC"
                    )
                }
            }
            // ⚠ Bug #3 fix: маршрут изменён с "weatherMap" на "weatherMap/{city}",
            // чтобы передавать название города в WeatherMapScreen.
            // Ранее city = "" (пустая строка) → WeatherMapViewModel.loadWeatherData("")
            // не могла получить координаты → карта не центрировалась на городе.
            // Город извлекается из backStackEntry.arguments и передаётся
            // напрямую в WeatherMapScreen, который загружает данные через
            // WeatherMapViewModel.loadWeatherData(city).
            composable(
                route = "weatherMap/{city}",
                arguments = listOf(navArgument("city") { type = NavType.StringType })
            ) { backStackEntry ->
                val city = backStackEntry.arguments?.getString("city") ?: ""
                val mapViewModel: WeatherMapViewModel = hiltViewModel()
                WeatherMapScreen(
                    city = city,
                    viewModel = mapViewModel,
                    navController = navController
                )
            }
        }
    }

    private fun requestLocationPermissionsIfNeeded() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, PERMISSION_REQUEST_CODE)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val granted = grantResults.any { it == PackageManager.PERMISSION_GRANTED }
            if (granted) {
                Toast.makeText(this, "Location permissions granted", Toast.LENGTH_SHORT).show()
                // Notify both ViewModels to retry location detection
                mapViewModel.retryDeviceLocation()
                forecastViewModel.retryDeviceLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permissions denied. Using default location.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return false
    }
}
