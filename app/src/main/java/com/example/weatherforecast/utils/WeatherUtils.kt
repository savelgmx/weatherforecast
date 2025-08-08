package com.example.weatherforecast.utils


import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherforecast.R
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.theme.QuickSandTypography
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherUtils {

    companion object {

        fun updateTemperature(temperature: Int, switchState: Boolean): String {
            val unitAbbreviation = if(switchState) "C° " else "F° "

            val temp = if (switchState) {
                "$temperature$unitAbbreviation"
            } else {
                val fahrenheitTemp = (temperature * 9 / 5) + 32
                "$fahrenheitTemp$unitAbbreviation"
            }
            return temp
        }

        @Composable
        fun updateMinMaxPressureValue(minMaxPressure:Int):Int{
            //this function returns max possible barometic pressure
            // , recalculating in chosen measurements unints
            //val maxPressure=1033 min pressure =870 //base max pressure value is constant in mbar units
            val localcontext= LocalContext.current
            val selectedPressureOption by DataStoreManager.pressurePrefFlow(localcontext).collectAsState(initial = 0)
            val pressureUnitsToSelect = localcontext.resources.getStringArray(R.array.pressure_units)
            // Check for a valid index
            if (selectedPressureOption < 0 || selectedPressureOption >= pressureUnitsToSelect.size) {
                return -1
            }
            val minMaxPressureValue = when(selectedPressureOption){
                0 -> minMaxPressure * 0.7500615613 // Conversion from mBar to mm Hg
                1 -> minMaxPressure * 0.029529983071445 // Conversion from mBar to inches Hg
                2 -> minMaxPressure // 1 mBar is equivalent to 1 hPa
                3 -> minMaxPressure // mBar is already the default unit
                else -> return -1
            }
            return minMaxPressureValue.toInt()
        }

        @Composable
        fun updatePressureUnit():String{
            val localcontext= LocalContext.current
            val selectedPressureOption by DataStoreManager.pressurePrefFlow(localcontext).collectAsState(initial = 0)
            val pressureUnitsToSelect = localcontext.resources.getStringArray(R.array.pressure_units)

            // Check for a valid index
            if (selectedPressureOption < 0 || selectedPressureOption >= pressureUnitsToSelect.size) {
                return "Invalid unit index"
            }

            // Get the chosen unit
            val chosenUnit = pressureUnitsToSelect[selectedPressureOption]
            return chosenUnit

        }

        @Composable
        fun updatePressure(pressureValue: Int): Int {
            val localcontext= LocalContext.current
            val selectedPressureOption by DataStoreManager.pressurePrefFlow(localcontext).collectAsState(initial = 0)
            val pressureUnitsToSelect = localcontext.resources.getStringArray(R.array.pressure_units)

            // Check for a valid index
            if (selectedPressureOption < 0 || selectedPressureOption >= pressureUnitsToSelect.size) {
                return -1// "Invalid unit index"
            }
            // Perform conversion based on the chosen unit
            val convertedPressure = when (selectedPressureOption) {
                0 -> pressureValue * 0.7500615613 // Conversion from mBar to mm Hg
                1 -> pressureValue * 0.029529983071445 // Conversion from mBar to inches Hg
                2 -> pressureValue // 1 mBar is equivalent to 1 hPa
                3 -> pressureValue // mBar is already the default unit
                else -> return -1 //"Invalid unit"
            }
            // Format the pressure value
            val formattedPressure = convertedPressure.toInt()
            // Return the result with unit
            return formattedPressure //" $formattedPressure $chosenUnit"
        }

        @Composable
        fun updateWind(windDirection: String, windSpeed: Int, context: Context): String {
            // Obtain the selected wind speed unit from preferences
            val selectedWindOptions by DataStoreManager.windPrefFlow(context).collectAsState(initial = 0)
            val windSpeedUnitsToSelect=context.resources.getStringArray(R.array.wind_speed_units)
            // Ensure the selected index is valid
            if (selectedWindOptions < 0 || selectedWindOptions >= windSpeedUnitsToSelect.size) {
                return "Invalid unit index"
            }
            val wind = degToCompass(windDirection.toInt(), context)
            // Create the wind string
            val windString = wind //$convertedWindSpeed, $unitAbbreviation

            return windString
        }

        fun convertWindSpeed(windSpeed:Int,selectedWindOptions:Int):String {
            // Convert the wind speed to the selected unit using integer calculations
            val convertedWindSpeed = when (selectedWindOptions) {
                0 ->  windSpeed  // km/h is the default unit
                1 -> (windSpeed/3.6).toInt() // Conversion from km/h to m/s
                2 -> (windSpeed*0.587).toInt() // Conversion from km/h to knots
                3 -> (windSpeed * 0.91).toInt()  // Conversion from km/h to ft/s
                else -> {}
            }
            return convertedWindSpeed.toString()
        }


        // Helper function to convert degrees to compass direction
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

        fun getMoonPhaseIconName(context: Context, phase: Double): Int {

            val moonIcons = arrayOf(
                R.drawable.moon_new,
                R.drawable.moon_waxing_crescent,
                R.drawable.moon_last_quarter,
                R.drawable.moon_waxing_gibbous,
                R.drawable.moon_full,
                R.drawable.moon_waning_gibbous,
                R.drawable.moon_first_quarter,
                R.drawable.moon_waning_crescent,
                R.drawable.moon_new
            )

            if (phase < 0.0 || phase > 1.0) {
                return R.drawable.moon_crescent
            }
            /*          val index = (phase * (moonIcons.size - 1)).toInt()
                      return moonIcons[index]*/
            return when {
                //phase < 0.0 || phase > 1.0 -> wrongValue
                phase == 0.0 -> moonIcons[0]
                phase < 0.25 -> moonIcons[1]
                phase == 0.25 -> moonIcons[2]
                phase < 0.5 -> moonIcons[3]
                phase == 0.5 -> moonIcons[4]
                phase < 0.75 -> moonIcons[5]
                phase == 0.75 -> moonIcons[6]
                phase < 1.0 -> moonIcons[7]
                phase == 1.0 -> moonIcons[8]
                else -> R.drawable.moon_crescent
            }
        }

        @Composable
        fun selectionWindSignature(selection:Int): String {
            val context= LocalContext.current
            val windSpeedUnitsToSelect= context.resources.getStringArray(R.array.wind_speed_units) //arrayOf("km/h", "m/s", "knots", "ft/s")
            // Get the chosen unit
            val selectedSignature = windSpeedUnitsToSelect[selection]
            return selectedSignature
        }

        @Composable
        fun selectionPressureSignature(selection:Int): String {
            val context= LocalContext.current
            val pressureUnitsToSelect= context.resources.getStringArray(R.array.pressure_units)//arrayOf("mm Hg", "inches Hg", "hPa", "mbar")
            val selectedSignature=pressureUnitsToSelect[selection]
            return selectedSignature
        }

        fun updateUVLevel(context: Context,uvLevel:Int):String{

            val UVDescriptions= context.resources.getStringArray(R.array.uv_index_values)
            // Check for a valid index
            if (uvLevel < 0 ) { return "Invalid unit index"}
            //now choose right uv value from array
            return when{
                uvLevel <=2             -> UVDescriptions[0] // low 2 or less
                uvLevel in(3..5)  -> UVDescriptions[1]//average 3-5
                uvLevel in(6..7)  -> UVDescriptions[2]//high 6-7
                uvLevel in(8..10) -> UVDescriptions[3]//very high 8-10
                uvLevel >=11            -> UVDescriptions[4]//extreme 11 and higher
                else -> {context.resources.getString(R.string.wrong_value)}
            }
        }

        fun calculateDawnAndDusk(sunrise: Int?, sunset: Int?): Array<String?> {
            // Constants for dawn and dusk offsets (30 minutes before and after) in seconds
            val dawnOffset = -30 * 60  // 30 minutes BEFORE sunrise in seconds
            val duskOffset = 30 * 60   // 30 minutes AFTER sunset in seconds

            // Calculate dawn and dusk times in seconds
            val dawnTime = sunrise?.plus(dawnOffset)
            val duskTime = sunset?.plus(duskOffset)

            // Convert to milliseconds for formatting
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val dawnString = dawnTime?.let { Date(it * 1000L) }?.let { dateFormat.format(it) }
            val duskString = duskTime?.let { Date(it * 1000L) }?.let { dateFormat.format(it) }

            return arrayOf(dawnString, duskString)
        }
        @Composable
        fun WeatherText(
            text: String,
            style: TextStyle,
            modifier: Modifier = Modifier
        ) {
            Text(
                text = text,
                style = style,
                color = MaterialTheme.colors.surface,
                modifier = modifier
            )
        }

        @Composable
        fun WeatherHeader(
            text: String,
            modifier: Modifier = Modifier.padding(start = 20.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = QuickSandTypography.titleMedium,
                color = MaterialTheme.colors.onPrimary,
                modifier = modifier
            )
        }

        /**
         * Gets the city name from DataStore preferences or device location
         * @param context Application context
         * @return City name or empty string if not available
         */
        suspend fun getCityName(context: Context): String {
            // First check DataStore for saved city
            val savedCity = DataStoreManager.cityNamePrefFlow(context).first()
            if (!savedCity.isNullOrBlank()) {
                return savedCity
            }

            // If no saved city, try device location
            val defineLocation = DefineDeviceLocation(context)
            val locationArray = defineLocation.getLocation()
            if (locationArray.isNotEmpty() && locationArray.size == 3) {
                return locationArray[2] ?: ""
            }

            return ""
        }

        /**
         * Saves city name to DataStore preferences
         * @param context Application context
         * @param cityName City name to save
         */
        suspend fun saveCityName(context: Context, cityName: String) {
            DataStoreManager.updateCityName(context, cityName)
        }
    }

}
