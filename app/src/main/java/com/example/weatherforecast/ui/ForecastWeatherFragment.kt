package com.example.weatherforecast.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherforecast.R
import com.example.weatherforecast.components.CurrentWeatherCard
import com.example.weatherforecast.components.ForecastWeatherList
import com.example.weatherforecast.components.HourlyWeatherRow
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.ui.viewmodel.OpenWeatherForecastViewModel
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.UIUtils
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

                val currentState=currentViewModel.weatherLiveData.value
                Log.d("current weather fragment response",currentState.toString())

                val forecastState=viewModel.forecastLiveData.value
                Log.d("weather fragment response",forecastState.toString())




                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all=16.dp)
                        .background(Blue300)
                )
                {
                    if (currentState != null) {
                        CurrentWeatherCard(weatherState = currentState)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(context.resources.getString(R.string.weather_24_hour),//"Погода на сутки",
                        fontWeight = FontWeight.Bold,
                        style= QuickSandTypography.subtitle1,
                        color = Color.White

                    )
                    //And now include hourly UI here
                    forecastState?.data?.hourly.let {
                        if (it != null) {
                            HourlyWeatherRow(it)
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))
                    Text(context.resources.getString(R.string.weather_7_days),//"Погода на 7 дней"
                        fontWeight = FontWeight.Bold,
                        style = QuickSandTypography.subtitle1,
                        color=Color.White
                     )

                    ForecastWeatherList(forecastState = forecastState)
                }

            }
        }
    }
}