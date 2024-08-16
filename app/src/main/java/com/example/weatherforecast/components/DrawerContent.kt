package com.example.weatherforecast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.utils.WeatherUtils
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val switchState by DataStoreManager.tempSwitchPrefFlow(context).collectAsState(initial = false)

    val selectedWindOption by DataStoreManager.windPrefFlow(context).collectAsState(initial = 0)
    var windSpeedUnitsToSelect= context.resources.getStringArray(R.array.wind_speed_units) //arrayOf("km/h", "m/s", "knots", "ft/s")
    var windSpeedUnitsPopup by remember { mutableStateOf(false) }

    val selectedPressureOption by DataStoreManager.pressurePrefFlow(context).collectAsState(initial = 0)
    var pressureUnitsToSelect= context.resources.getStringArray(R.array.pressure_units)//arrayOf("mm Hg", "inches Hg", "hPa", "mbar")
    var pressureUnitsPopup by remember { mutableStateOf(false) }

    val selectedPrecipateOption by DataStoreManager.precipatePrefFlow(context).collectAsState(initial = 0)
    var precipateUnitsToSelect= arrayOf("mm.", "inches")
    var precipateUnitsPopup by remember { mutableStateOf(false) }


    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(25.dp)
                .background(Blue300)
        ) {

        }
        HorizontalDivider()

        Row(

            Modifier.padding(all = 8.dp)
                .clickable(onClick = { windSpeedUnitsPopup = true }) ,
        )
        {
            Column {
                Text(text = "Measure Units of Wind speed")
                if (windSpeedUnitsPopup)
                {
                    AlertDialog(
                        onDismissRequest = { windSpeedUnitsPopup = false },
                        title = { Text("Choose an option") },
                        text = {
                            Column {
                                RadioButtonGroup(
                                    selectedOption = selectedWindOption,
                                    optionsToSelect = windSpeedUnitsToSelect,
                                    onOptionSelected = { option ->
                                        scope.launch {
                                            DataStoreManager.updateWindPref(context, option)
                                            windSpeedUnitsPopup = false
                                        }
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { windSpeedUnitsPopup = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
            Column { Text(" "+WeatherUtils.selectionWindSignature(selectedWindOption)) }
        }

        HorizontalDivider()

        Row(
            Modifier.padding(all = 8.dp)
                .clickable(onClick = { pressureUnitsPopup = true }) ,
        )
        {
            Column{
                Text(text = "Measure Units of Pressure")
                if (pressureUnitsPopup)
                {
                    AlertDialog(
                        onDismissRequest = { pressureUnitsPopup = false },
                        title = { Text("Choose an option") },
                        text = {
                            Column {
                                RadioButtonGroup(
                                    selectedOption = selectedPressureOption,
                                    optionsToSelect = pressureUnitsToSelect,
                                    onOptionSelected = { option ->
                                        scope.launch {
                                            DataStoreManager.updatePressurePref(context, option)
                                            pressureUnitsPopup = false
                                        }
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { pressureUnitsPopup = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

            }
            Column{
                Text(" "+ WeatherUtils.selectionPressureSignature(selectedPressureOption))
            }
        }
        HorizontalDivider()
        Row(

            Modifier.padding(all = 8.dp)
                .clickable(onClick = { precipateUnitsPopup = true }) ,

            ) {

            Text(text = "Measure Units of Precipate")

            if (precipateUnitsPopup)
            {
                AlertDialog(
                    onDismissRequest = { precipateUnitsPopup = false },
                    title = { Text("Choose an option") },
                    text = {
                        Column {
                            RadioButtonGroup(
                                selectedOption = selectedPrecipateOption,
                                optionsToSelect = precipateUnitsToSelect,
                                onOptionSelected = { option ->
                                    scope.launch {
                                        DataStoreManager.updatePrecipatePref(context, option)
                                        precipateUnitsPopup = false
                                    }
                                }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { precipateUnitsPopup = false }) {
                            Text("Close")
                        }
                    }
                )
            }


        }
        HorizontalDivider()

        Row(modifier = Modifier
            .padding(8.dp)

        ) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("F")

            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Switch(
                    checked = switchState,
                    onCheckedChange = { isChecked ->
                        scope.launch {
                            DataStoreManager.updateSwitchPref(context, isChecked)
                        }
                    }
                )

            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("C")

            }
        }

        HorizontalDivider()

    }
}



