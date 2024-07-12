package com.example.weatherforecast.components

// SettingsScreen.kt navController: NavController

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    temperatureUnits: String,
    distanceUnits: String,
    onTemperatureUnitsChange: (String) -> Unit,
    onDistanceUnitsChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val screenWidth = LocalDensity.current.run { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    offsetX += dragAmount
                    if (offsetX > screenWidth / 2) {
                        scope.launch {
                            onDismiss()
                        }
                    } else if (offsetX < -screenWidth / 2) {
                        // Handle swipe left to dismiss
                        onDismiss()
                    }
                }
            }
            .background(Color.Transparent)
            .clickable(onClick = { onDismiss() })
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize() // Adjusts to fit content
                .offset(x = offsetX.dp)
                .background(MaterialTheme.colors.surface)
                .clickable(onClick = { /* Consume clicks inside the surface */ }),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Настройки", style = MaterialTheme.typography.h5)
                Spacer(modifier = Modifier.height(16.dp))
                TemperatureUnitToggle(temperatureUnits, onTemperatureUnitsChange)
                Spacer(modifier = Modifier.height(16.dp))
                SettingsOption("Temperature Units", temperatureUnits, onTemperatureUnitsChange)
                SettingsOption("Distance Units", distanceUnits, onDistanceUnitsChange)
            }
        }
    }
}

@Composable
fun TemperatureUnitToggle(selectedOption: String, onOptionSelected: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Temperature", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.width(8.dp))
        ToggleButton(selectedOption == "Celsius", "C") { onOptionSelected("Celsius") }
        Spacer(modifier = Modifier.width(4.dp))
        ToggleButton(selectedOption == "Fahrenheit", "F") { onOptionSelected("Fahrenheit") }
    }
}

@Composable
fun ToggleButton(isSelected: Boolean, text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .padding(4.dp)
            .background(
                if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        color = if (isSelected) Color.White else MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.button
    )
}

@Composable
fun SettingsOption(title: String, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        DropdownMenu(selectedOption, onOptionSelected)
    }
}

@Composable
fun DropdownMenu(selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Metric", "Imperial")

    Box {
        Text(
            text = selectedOption,
            modifier = Modifier
                .clickable { expanded = true }
                .background(MaterialTheme.colors.surface)
                .padding(8.dp)
        )
        androidx.compose.material.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}