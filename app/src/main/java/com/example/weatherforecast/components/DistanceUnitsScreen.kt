package com.example.weatherforecast.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DistanceUnitsScreen(
    onDistanceUnitsChange: (String) -> Unit,
    onWindSpeedChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val distanceUnits = listOf("metric", "imperial")
    val windSpeedUnits = listOf("km/h", "m/s", "knots", "ft/s")

    var selectedDistanceUnit by remember { mutableStateOf("metric") }
    var selectedWindSpeedUnit by remember { mutableStateOf("km/h") }

    Column {
        Text(text = "Choose Distance Units")
        distanceUnits.forEach { unit ->
            RadioButton(
                selected = unit == selectedDistanceUnit,
                onClick = { selectedDistanceUnit = unit }
            )
            Text(text = unit)
        }

        Text(text = "Choose Wind Speed Units")
        windSpeedUnits.forEach { unit ->
                RadioButton(
                selected = unit == selectedWindSpeedUnit,
                onClick = { selectedWindSpeedUnit = unit }
                )
                Text(text = unit)
            }

        Button(onClick = {
            onDistanceUnitsChange(selectedDistanceUnit)
            onWindSpeedChange(selectedWindSpeedUnit)
                onDone()
        }) {
            Text("Done")
        }
    }
}
