package com.example.weatherforecast.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherforecast.components.DailyWeatherForecast
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherForecastViewModel
import dagger.hilt.android.AndroidEntryPoint
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

@AndroidEntryPoint
class ForecastDetailFragment : Fragment() {

    private val args: ForecastDetailFragmentArgs by navArgs()
    private val viewModel: OpenWeatherForecastViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val forecastState by viewModel.forecastLiveData
                val dailyList = forecastState.data?.daily ?: emptyList()
                val initialIndex = args.index
                if (dailyList.isNotEmpty() && initialIndex in 0 until dailyList.size) {
                    DailyWeatherForecast(
                        navController = findNavController(),
                        dailyList = dailyList,
                        startIndex = initialIndex
                    )
                } else {
                   // Text("No data available")
                }
            }
        }
    }
}