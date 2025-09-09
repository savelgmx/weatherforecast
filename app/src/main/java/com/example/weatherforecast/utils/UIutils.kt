package com.example.weatherforecast.utils

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

class UIUtils {
    companion object {
        val iconurl = AppConstants.WEATHER_API_IMAGE_ENDPOINT
        fun getMockWeatherCard(): WeatherResponse {
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
                "25200.00",
                1502026,
                "Красноярск",
                200
            )
        }

        fun getMockForecastlist(): ForecastResponse {
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
                        1, listOf(Weather(804, "Clouds", "пасмурно", "04n")),
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
                56.0097, 92.79, "Asia/Krasnoyarsk"
            )
        }

        fun getMockDailyWeather():Daily{
            return Daily(
                100, -18.32, 1708754400,
                FeelsLike(-19.14, -16.77, -27.53, -19.14),
                73, 0.3, 1708862520, 1708824420, 1036,
                1708736117, 1708772966,
                Temp(-19.14, -16.77, -27.53, -19.14, -20.0, -24.4),
                1, listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                224, 1.37, 2.40
            )

        }

        fun getMockHourlylist(): List<Hourly> {
            return listOf(
                Hourly(
                    99, -18.32, 1708774497, -17.78, 95,
                    1037, -21.2, 0.0, 10000,
                    listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                    223, 2.2, 1.8
                ),
                Hourly(
                    99, -18.32, 1708774497, -17.78, 95,
                    1037, -21.2, 0.0, 10000,
                    listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                    223, 2.2, 1.8
                ),
                Hourly(
                    99, -18.32, 1708774497, -17.78, 95,
                    1037, -21.2, 0.0, 10000,
                    listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                    223, 2.2, 1.8
                )


            )


        }


    }
}







