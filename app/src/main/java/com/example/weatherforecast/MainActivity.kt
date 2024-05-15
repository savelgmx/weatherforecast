package com.example.weatherforecast


import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Current
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Temp
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.ui.theme.WeatherforecastTheme
import com.example.weatherforecast.ui.viewmodel.OpenWeatherForecastViewModel
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherforecast.utils.UIUtils
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

}

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related operations
                // You might want to call the weather and forecast APIs here
            } else {
                // Permission denied, handle accordingly (e.g., show explanation, disable functionality)
            }
        }
    }


    @Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
    @Composable
    fun WeatherUISuccessPreview() {
        val successState = Resource.Success(getMockWeatherResponse())
        val successForecastState = Resource.Success(getMockForecastResponse())

        UIUtils.WeatherUI(successState, successForecastState)
        UIUtils.ForecastUI(successForecastState)
    }

    fun getMockWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            Coord(92.7917, 56.0097),
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            "stations",
            Main(-21.77, -28.77, -22.78, -21.77, 1040, 85),
            10000,
            Wind(2.73, 228),
            Clouds(100),
            1708170884,
            Sys(2, 2088371, "RU", 1708132314, 1708167250),
            25200,
            1502026,
            "Красноярск",
            200
        )
    }

    fun getMockForecastResponse(): ForecastResponse {
        return ForecastResponse(

            Current(
                100, -18.32, 1708774497, -17.78, 95, 1037,
                1708736117, 1708772966, -17.78, 0.0, 10000,
                listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                228, 0.6, 6.0
            ),
            listOf(
                Daily(
                    100, -18.32, 1708754400,
                    FeelsLike(-19.14, -16.77, -27.53, -19.14),
                    73, 0.5, 1708862520, 1708824420, 1036,
                    1708736117, 1708772966,
                    Temp(-19.14, -16.77, -27.53, -19.14, -20.0, -24.4),
                    0.5, listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                    224, 1.37, 2.40
                )
            ),
            listOf(
                Hourly(
                    99, -18.32, 1708774497, -17.78, 95,
                    1037, -21.2, 0.0, 10000,
                    listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                    223, 2.2, 1.8
                )
            ),
            56.0097, 92.79, "Asia/Krasnoyarsk", 25200
        )
    }
}




