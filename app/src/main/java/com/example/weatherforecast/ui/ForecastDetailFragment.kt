package com.example.weatherforecast.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.weatherforecast.components.DailyWeatherForecast
import dagger.hilt.android.AndroidEntryPoint

import com.example.weatherforecast.ui.ForecastWeatherFragmentDirections
@AndroidEntryPoint
class ForecastDetailFragment : Fragment() {

    private val args: ForecastDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val daily = args.daily
                DailyWeatherForecast(daily)
            }
        }
    }
}