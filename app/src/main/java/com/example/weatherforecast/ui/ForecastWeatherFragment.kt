package com.example.weatherforecast.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.components.CurrentWeatherCard
import com.example.weatherforecast.components.ForecastWeatherList
import com.example.weatherforecast.components.HourlyWeatherRow
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.ui.viewmodel.OpenWeatherForecastViewModel
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                val forecastState = viewModel.forecastLiveData.value
                val scrollState = rememberScrollState()
                val context = LocalContext.current

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                       // .padding(1.dp)
                        .background(Blue300)
                      //  .verticalScroll(scrollState) //vertical scroll exeption if uncommented
                ) {
                    item {
                        currentState?.let { weatherState ->
                            CurrentWeatherCard(weatherState = weatherState)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = context.resources.getString(R.string.weather_24_hour),
                            fontWeight = FontWeight.Bold,
                            style = QuickSandTypography.subtitle1,
                            color = Color.White,
                            modifier = Modifier.padding(start = 20.dp)

                        )
                    }

                    item {
                        forecastState?.data?.hourly?.let { hourlyWeatherList ->
                            HourlyWeatherRow(hourlyWeatherList)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = context.resources.getString(R.string.weather_7_days),
                            fontWeight = FontWeight.Bold,
                            style = QuickSandTypography.subtitle1,
                            color = Color.White, modifier = Modifier.padding(start = 20.dp)
                        )
                    }

                    item {
                        ForecastWeatherList(
                            forecastState = forecastState,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
