package com.example.weatherforecast.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.ui.viewmodel.OpenWeatherForecastViewModel
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import com.example.weatherforecast.utils.WeatherUtils
import dagger.hilt.android.AndroidEntryPoint

import com.example.weatherforecast.components.MainScreen

@AndroidEntryPoint
class ForecastWeatherFragment : Fragment() {

    private val viewModel: OpenWeatherForecastViewModel by viewModels()
    private val currentViewModel:OpenWeatherMapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()

                val currentState = currentViewModel.weatherLiveData.value
                Log.d("current weather fragment response", currentState.toString())

                val forecastState = viewModel.forecastLiveData.value
                Log.d("weather fragment response", forecastState.toString())

                val date = currentViewModel.weatherLiveData.value?.data?.dt?.let { WeatherUtils.updateDateToToday(it.toInt()) }
                val cityName = currentViewModel.weatherLiveData.value?.data?.name

                // Update the toolbar title
                (activity as MainActivity).updateToolbarTitle("$date $cityName")

                MainScreen(navController, currentState, forecastState)
                }
            }
        }

}