package com.example.weatherforecast.components

// SettingsScreen.kt navController: NavController

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
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
    temperatureUnits: Boolean, // Changed to Boolean
    distanceUnits: String,
    onTemperatureUnitsChange: (Boolean) -> Unit, // Changed to Boolean
    onDistanceUnitsChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val screenWidth = LocalDensity.current.run { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    val scope = rememberCoroutineScope()
    val dragAmountThreshold = screenWidth / 4

    var dropdownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    offsetX += dragAmount
                    if (offsetX > dragAmountThreshold) {
                        scope.launch { onDismiss() }
                    } else if (offsetX < -dragAmountThreshold) {
                        scope.launch { onDismiss() }
                    }
                }
            }
            .background(Color.Transparent)
            .clickable(onClick = { onDismiss() })
    ) {
        Surface(
            modifier = Modifier
                .width((LocalConfiguration.current.screenWidthDp.dp) * 3 / 4)
                .offset(x = offsetX.dp)
                .background(MaterialTheme.colors.surface)
                .clickable(onClick = { /* Consume clicks inside the surface */ }),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text("Temperature Units", modifier = Modifier.weight(1f))
                    Switch(
                        checked = temperatureUnits,
                        onCheckedChange = { isChecked ->
                            onTemperatureUnitsChange(isChecked)
                        }
                    )
                    Text(if (temperatureUnits) "Celsius" else "Fahrenheit")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .clickable { dropdownExpanded = !dropdownExpanded }
                ) {
                    Text("Distance Units", modifier = Modifier.weight(1f))
                    Text(distanceUnits)
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        onDistanceUnitsChange("metric")
                        dropdownExpanded = false
                    }) {
                        Text("Metric")
                    }
                    DropdownMenuItem(onClick = {
                        onDistanceUnitsChange("imperial")
                        dropdownExpanded = false
                    }) {
                        Text("Imperial")
                    }
                }
            }
        }
    }
}
