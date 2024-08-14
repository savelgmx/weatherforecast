package com.example.weatherforecast.utils


import android.content.Context
import com.example.weatherforecast.R
import com.example.weatherforecast.ui.viewmodel.SharedViewModelHolder
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherUtils {

    companion object {

        private var isMetric: Boolean = true


        init {
            SharedViewModelHolder.sharedViewModel.temperatureUnitsLiveData.observeForever { isCelsius ->
                isMetric = isCelsius
            }
            SharedViewModelHolder.sharedViewModel.distanceUnitsLiveData.observeForever { units ->
                isMetric = units == "metric"
            }



        }

        fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
            return if (isMetric) metric else imperial
        }

        fun updateTemperature(temperature: Int, localContext: Context): String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation("°C", "°F")
            val temp = if (isMetric) {
                "$temperature$unitAbbreviation"
            } else {
                val fahrenheitTemp = (temperature * 9 / 5) + 32
                "$fahrenheitTemp$unitAbbreviation"
            }
            return temp
        }

        fun updatePressure(pressureValue: Int): String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation("mm", "in")
            val pressure = if (isMetric) {
                "$pressureValue $unitAbbreviation"
            } else {
                val pressureInInches = pressureValue / 25.4
                "${"%.2f".format(pressureInInches)} $unitAbbreviation"
            }
            return pressure
        }

        fun updateWind(windDirection: String, windSpeed: Int, context: Context): String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation("m/sec.", "mph")
            val windSpeedValue = if (isMetric) {
                windSpeed
            } else {
                (windSpeed * 2.237).toInt()
            }
            val wind = degToCompass((windDirection).toInt(), context)
            val windstring = "$wind , $windSpeed $unitAbbreviation"
            return windstring
        }


        private fun degToCompass(num: Int, context: Context): String {
            val winDir = Math.floor((num / 22.5) + 0.5).toInt()
            val directions =
                context.resources.getStringArray(R.array.directions_array) // Load the array from resources
            return directions[(winDir % 16).toInt()]
        }

        fun updateDateToToday(dt: Int?): String {
            //API returns date/time as a UnixEpoc integer timestamp
            //we must transform this with datetime format

            val simpleDateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault())
            var today: String = "Today"
            if (dt != null) {
                today = simpleDateFormat.format(dt * 1000L)
            }

            return today
        }

        fun updateTime(dt: Int?): String {
            //API returns date/time as a UnixEpoc integer timestamp
            //we must transform this with datetime format with time zone offset relatively to GMT into human readable date/time

            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            var currentTime = "07:00"
            if (dt != null) {
                currentTime = simpleDateFormat.format(dt * 1000L)
            }
            return currentTime
        }

        fun calculateMoonPhase(context: Context, phase: Double): String {
            val wrongValue = context.resources.getString(R.string.wrong_value)
            val moonSymbols = context.resources.getStringArray(R.array.moon_symbols_array)
            val moonDescriptions = arrayOf(
                context.getString(R.string.new_moon),
                context.getString(R.string.waxing_crescent),
                context.getString(R.string.first_quarter),
                context.getString(R.string.waxing_gibbous),
                context.getString(R.string.full_moon),
                context.getString(R.string.waning_gibbous),
                context.getString(R.string.last_quarter),
                context.getString(R.string.waning_crescent),
                context.getString(R.string.new_moon)
            )

            if (phase < 0.0 || phase > 1.0) {
                return "Ungültiger Wert"
            }

            return when {
                //phase < 0.0 || phase > 1.0 -> wrongValue
                phase == 0.0 -> "${moonSymbols[0]} ${moonDescriptions[0]}"
                phase < 0.25 -> "${moonSymbols[1]} ${moonDescriptions[1]}"
                phase == 0.25 -> "${moonSymbols[2]} ${moonDescriptions[2]}"
                phase < 0.5 -> "${moonSymbols[3]} ${moonDescriptions[3]}"
                phase == 0.5 -> "${moonSymbols[4]} ${moonDescriptions[4]}"
                phase < 0.75 -> "${moonSymbols[5]} ${moonDescriptions[5]}"
                phase == 0.75 -> "${moonSymbols[6]} ${moonDescriptions[6]}"
                phase < 1.0 -> "${moonSymbols[7]} ${moonDescriptions[7]}"
                phase == 1.0 -> "${moonSymbols[8]} ${moonDescriptions[8]}"
                else -> wrongValue
            }
        }

        fun SelectionWindSignature(selection:Int): String {
            val selectedSignature: String = when (selection) {
                0 -> "km/h"
                1 -> "m/s"
                2 -> "knots"
                3 -> "ft/s"
                else -> "Unknown" // Handle unexpected values
            }
            return selectedSignature
        }

        fun SelectionPressureSignature(selection:Int): String {
            val selectedSignature: String = when (selection) {
                0 -> "mm Hg"
                1 ->  "inches Hg"
                2 -> "hPa"
                3 -> "mbar"
                else -> "Unknown" // Handle unexpected values
            }
            return selectedSignature
        }

        fun SelectionPrecipeSignature(selection:Int): String {
            val selectedSignature: String = when (selection) {
                0 -> "mm."
                1 ->  "inches"
                else -> "Unknown" // Handle unexpected values
            }
            return selectedSignature
        }

    }
}
