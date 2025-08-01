package com.example.weatherforecast.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.components.CityDialog
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.components.DrawerContent
import com.example.weatherforecast.components.MainScreen
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherForecastViewModel
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherMapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ForecastWeatherFragment : Fragment() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var getDeviceCityUseCase: GetDeviceCityUseCase

    /*
    Each fragment uses by viewModels(),
    creating separate instances of OpenWeatherForecastViewModel.
    When navigating to ForecastDetailFragment, a new instance is created,
    and if it fetches data in its initialization (e.g., in init block),
    this triggers an API call, causing the delay.
    Scope the ViewModel to the activity using by activityViewModels()
     in both ForecastWeatherFragment and ForecastDetailFragment.
     This ensures both fragments share the same ViewModel instance.
     This way, data fetched in ForecastWeatherFragment (e.g., the 15-day forecast)
     is already available when navigating to ForecastDetailFragment, eliminating the need for a new API call.

    */
    private val viewModel: OpenWeatherForecastViewModel by activityViewModels()
    private val currentViewModel: OpenWeatherMapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val scope = rememberCoroutineScope()
                val enteredCity by dataStoreManager.cityNamePrefFlow(requireContext()).collectAsState(initial = "")
                if (enteredCity?.isEmpty() == true) {
                    var deviceCity: String? by remember { mutableStateOf(null) }
                    LaunchedEffect(Unit) {
                        try {
                            deviceCity = withContext(scope.coroutineContext) {
                                getDeviceCityUseCase.execute()
                            }
                            Log.d("ForecastWeatherFragment", "Device City: $deviceCity")
                        } catch (e: Exception) {
                            Log.e("ForecastWeatherFragment", "Error fetching device city", e)
                            deviceCity = null
                        }
                    }
                    if (deviceCity != null) {
                        LaunchedEffect(deviceCity) {
                            Log.d("ForecastWeatherFragment", "Device city add: $deviceCity")
                            try {
                            dataStoreManager.updateCityName(requireContext(), deviceCity!!)
                                Log.d("ForecastWeatherFragment", "City updated in DataStore: $deviceCity")
                            } catch (e: Exception) {
                                Log.e("ForecastWeatherFragment", "Failed to update city in DataStore", e)
                            }
                        }
                    } else {
                        CityDialog(
                            initialCity = "",
                            onCityChange = { newCity ->
                                scope.launch {
                                    try {
                                    dataStoreManager.updateCityName(requireContext(), newCity)
                                        Log.d("ForecastWeatherFragment", "City updated via Dialog: $newCity")
                                    } catch (e: Exception) {
                                        Log.e("ForecastWeatherFragment", "Failed to update city via Dialog", e)
                                    }
                                }
                            },
                            onDismiss = { }
                        )
                    }
                }
                val navController = findNavController()
                val currentState = currentViewModel.weatherLiveData.value
                val forecastState = viewModel.forecastLiveData.value
                DrawerContent()
                MainScreen(
                    navController = navController,
                    currentState = currentState,
                    forecastState = forecastState,
                    onRefresh = {
                        currentViewModel.refreshWeather()
                        viewModel.refreshWeather()
                    }
                )

            }
        }
    }
}
