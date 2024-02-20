package com.example.weatherforecast.utils


import android.content.Context
import com.example.weatherforecast.R
import java.text.SimpleDateFormat
import java.util.*

class WeatherUtils {

    companion object {

        private var latitude: Double = 0.0
        private var longitude:Double = 0.0
        private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
            // return if (viewModel.isMetricUnit) metric else imperial
            return metric
        }

        fun formatDateSubtitle(dt: Int?): String {
            val simpleDateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault())
            return if (dt != null) {
                simpleDateFormat.format(dt * 1000L)
            } else {
                "Today" // Or another default value if dt is null
            }
        }

        //implement latitude and longitude store
        fun setLongitude(lon:Double){
            this.longitude=lon
        }
        fun getLongitude():Double{
            return longitude
        }
        fun setLatitude(lat:Double){
            this.latitude=lat
        }
        fun getLatitude():Double{
            return latitude
        }

        // Implement other common update functions here
        // updateTemperatures, updateCondition, updatePressure, updateWind, updateVisibility, degToCompass, etc.
        fun updateTemperature(temperature: Int):String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation("°C", "°F")
           val temp = "$temperature$unitAbbreviation"
            return temp
        }

        fun updatePressure(pressureValue: Int):String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation("mm", "in")
            val pressure= "$pressureValue $unitAbbreviation"
            return pressure
        }

        fun updateWind(windDirection: String, windSpeed: Int,context: Context):String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation("m/sec.", "mph")
            val wind=degToCompass((windDirection).toInt(),context)
            val windstring  = "$wind , $windSpeed $unitAbbreviation"
            return windstring
        }


        private fun degToCompass(num: Int, context: Context): String {
            val winDir = Math.floor((num / 22.5) + 0.5).toInt()
            val directions = context.resources.getStringArray(R.array.directions_array) // Load the array from resources
            return directions[(winDir % 16).toInt()]
        }

        fun updateDateToToday(dt: Int?):String {
            //API returns date/time as a UnixEpoc integer timestamp
            //we must transform this with datetime format

            val simpleDateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault())
            var today:String="Today"
            if (dt != null) {
                today=simpleDateFormat.format(dt  * 1000L)
            }

            return today
        }

        fun updateTime(dt: Int?):String {
            //API returns date/time as a UnixEpoc integer timestamp
            //we must transform this with datetime format with time zone offset relatively to GMT into human readable date/time

            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            var currentTime="07:00"
            if (dt != null) {
                currentTime=simpleDateFormat.format(dt * 1000L)
            }
            return currentTime
        }



    }
}
