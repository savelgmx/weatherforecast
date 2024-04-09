package com.example.weatherforecast.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.ui.viewmodel.OpenWeatherForecastViewModel
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.UIUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastWeatherFragment : Fragment() {

    private val viewModel: OpenWeatherForecastViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val forecastState by remember { mutableStateOf<Resource<ForecastResponse>>(Resource.Loading()) }
                UIUtils.ForecastUI(forecastState)
            }
        }
    }
}