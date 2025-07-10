package com.example.weatherforecast.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherforecast.components.DailyWeatherForecast
import com.example.weatherforecast.presentation.viewmodels.OpenWeatherForecastViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastDetailFragment : Fragment() {

    private val args: ForecastDetailFragmentArgs by navArgs()
    private val viewModel: OpenWeatherForecastViewModel by viewModels()

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