package com.example.weatherforecast.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.components.MainScreen
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherForecastViewModel
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherMapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastWeatherFragment : Fragment() {
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
                val navController = findNavController()

                val currentState = currentViewModel.weatherLiveData.value
                val forecastState = viewModel.forecastLiveData.value
                MainScreen(
                    navController = navController,
                    currentState = currentState,
                    forecastState = forecastState,
                    onRefresh = {
                        currentViewModel.getCurrentWeather()
                        viewModel.getForecastWeather()
                    }
                )

            }
        }
    }
}
