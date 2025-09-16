package com.example.weatherforecast.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.components.WeatherMapScreen
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherMapFragment : Fragment() {

    // ViewModel для карты погоды (через Hilt)
    private val viewModel: WeatherMapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Используем ComposeView, так как экран карты у нас написан на Compose
        return ComposeView(requireContext()).apply {
            setContent {
                val context = LocalContext.current

                // Подписка на сохранённое название города из DataStore
                val city by DataStoreManager.cityNamePrefFlow(context)
                    .collectAsState(initial = "Amsterdam")

                // Встраиваем готовый Compose-экран WeatherMapScreen
                city?.let { WeatherMapScreen(city = it, viewModel = viewModel) }
            }
        }
    }
}
