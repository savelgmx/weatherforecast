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

object WeatherComposables {

    @Composable
    fun updateMinMaxPressureValue(minMaxPressure: Int): Int {
        //this function returns max possible barometic pressure
        // , recalculating in chosen measurements unints
        //val maxPressure=1033 min pressure =870 //base max pressure value is constant in mbar units
        val localcontext = LocalContext.current
        val selectedPressureOption by DataStoreManager.pressurePrefFlow(localcontext)
            .collectAsState(initial = 0)
        val pressureUnitsToSelect =
            localcontext.resources.getStringArray(R.array.pressure_units)
        // Check for a valid index
        if (selectedPressureOption < 0 || selectedPressureOption >= pressureUnitsToSelect.size) {
            return -1
        }
        val minMaxPressureValue = when (selectedPressureOption) {
            0 -> minMaxPressure * 0.7500615613 // Conversion from mBar to mm Hg
            1 -> minMaxPressure * 0.029529983071445 // Conversion from mBar to inches Hg
            2 -> minMaxPressure // 1 mBar is equivalent to 1 hPa
            3 -> minMaxPressure // mBar is already the default unit
            else -> return -1
        }
        return minMaxPressureValue.toInt()
    }

    @Composable
    fun updatePressureUnit(): String {
        val localcontext = LocalContext.current
        val selectedPressureOption by DataStoreManager.pressurePrefFlow(localcontext)
            .collectAsState(initial = 0)
        val pressureUnitsToSelect =
            localcontext.resources.getStringArray(R.array.pressure_units)

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
        val localcontext = LocalContext.current
        val selectedPressureOption by DataStoreManager.pressurePrefFlow(localcontext)
            .collectAsState(initial = 0)
        val pressureUnitsToSelect =
            localcontext.resources.getStringArray(R.array.pressure_units)

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
        val selectedWindOptions by DataStoreManager.windPrefFlow(context)
            .collectAsState(initial = 0)
        val windSpeedUnitsToSelect = context.resources.getStringArray(R.array.wind_speed_units)
        // Ensure the selected index is valid
        if (selectedWindOptions < 0 || selectedWindOptions >= windSpeedUnitsToSelect.size) {
            return "Invalid unit index"
        }
        val wind = degToCompass(windDirection.toInt(), context)
        // Create the wind string
        val windString = wind //$convertedWindSpeed, $unitAbbreviation

        return windString
    }

    // Helper function to convert degrees to compass direction
    private fun degToCompass(num: Int, context: Context): String {
        val winDir = Math.floor((num / 22.5) + 0.5).toInt()
        val directions =
            context.resources.getStringArray(R.array.directions_array) // Load the array from resources
        return directions[(winDir % 16).toInt()]
    }

    fun getAirQualityIconName(context: Context, aqiLevel: Int): Int {

        val aiqAirIcons = arrayOf(
            R.drawable.pollution_good,
            R.drawable.pollution_moderate,
            R.drawable.pollution_unhealthly,
            R.drawable.pollution_very_unhealthly,
            R.drawable.pollution_very_unhealthly
        )

        return when {
            aqiLevel <= 50 -> aiqAirIcons[0]
            aqiLevel <= 100 -> aiqAirIcons[1]
            aqiLevel <= 150 -> aiqAirIcons[2]
            aqiLevel <= 200 -> aiqAirIcons[3]
            aqiLevel <= 300 -> aiqAirIcons[4]
            else -> aiqAirIcons[4]
        }

    }

    @Composable
    fun selectionWindSignature(selection: Int): String {
        val context = LocalContext.current
        val windSpeedUnitsToSelect =
            context.resources.getStringArray(R.array.wind_speed_units) //arrayOf("km/h", "m/s", "knots", "ft/s")
        // Get the chosen unit
        val selectedSignature = windSpeedUnitsToSelect[selection]
        return selectedSignature
    }

    @Composable
    fun selectionPressureSignature(selection: Int): String {
        val context = LocalContext.current
        val pressureUnitsToSelect =
            context.resources.getStringArray(R.array.pressure_units)//arrayOf("mm Hg", "inches Hg", "hPa", "mbar")
        val selectedSignature = pressureUnitsToSelect[selection]
        return selectedSignature
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
}
