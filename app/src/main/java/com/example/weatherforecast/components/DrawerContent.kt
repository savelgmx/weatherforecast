package com.example.weatherforecast.components

import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherforecast.R

import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.WeatherUtils
import kotlinx.coroutines.launch

@Composable
fun DrawerContent() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Safe NavController extraction
    val activity = remember(context) {
        var ctx = context
        while (ctx is ContextWrapper && ctx !is FragmentActivity) {
            ctx = ctx.baseContext
        }
        ctx as? FragmentActivity
    }

    val navController = remember(activity) {
        activity?.let {
            val navHostFragment = it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            if (navHostFragment is NavHostFragment) {
                navHostFragment.navController
            } else null
        }
    }

    val switchState by DataStoreManager.tempSwitchPrefFlow(context).collectAsState(initial = true)

    val selectedWindOption by DataStoreManager.windPrefFlow(context).collectAsState(initial = 0)
    var windSpeedUnitsToSelect= context.resources.getStringArray(R.array.wind_speed_units) //arrayOf("km/h", "m/s", "knots", "ft/s")
    var windSpeedUnitsPopup by remember { mutableStateOf(false) }

    val selectedPressureOption by DataStoreManager.pressurePrefFlow(context).collectAsState(initial = 0)
    var pressureUnitsToSelect= context.resources.getStringArray(R.array.pressure_units)//arrayOf("mm Hg", "inches Hg", "hPa", "mbar")
    var pressureUnitsPopup by remember { mutableStateOf(false) }

    val enteredCity by DataStoreManager.cityNamePrefFlow(context).collectAsState(initial = "")//text field with entered city name
    var enteredCityPopup by remember { mutableStateOf(false) }


    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(35.dp)
                .background(Blue300)
        ) {
            Text(text=context.getString(R.string.measurements_units),
                color=Color.White,
                style = QuickSandTypography.titleMedium,
                modifier = Modifier.padding(all = 8.dp)
            )
        }
        HorizontalDivider()

        Row(

            Modifier
                .padding(all = 8.dp)
                .clickable(onClick = { windSpeedUnitsPopup = true }) ,
        )
        {
            Column( verticalArrangement = Arrangement.Center) {
                Icon(painter = painterResource(id = R.drawable.wind_24), contentDescription = "wind speed Icon")
            }

            Column {
                Text(text =" "+context.getString(R.string.measure_units_wind))
                if (windSpeedUnitsPopup)
                {
                    AlertDialog(
                        onDismissRequest = { windSpeedUnitsPopup = false },
                        title = { Text(context.getString(R.string.choose_option)) },
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
                                Text(context.getString(R.string.close),color= Color.White)
                            }
                        }
                    )
                }
            }
            Column { Text(" "+WeatherUtils.selectionWindSignature(selectedWindOption)) }
        }

        HorizontalDivider()

        Row(
            Modifier
                .padding(all = 8.dp)
                .clickable(onClick = { pressureUnitsPopup = true }) ,
        )
        {
            Column( verticalArrangement = Arrangement.Center) {
                Icon(painter = painterResource(id = R.drawable.dashboard_24), contentDescription = "pressure Icon")
            }

            Column{
                Text(" "+context.getString(R.string.measure_units_pressure))
                if (pressureUnitsPopup)
                {
                    AlertDialog(
                        onDismissRequest = { pressureUnitsPopup = false },
                        title = { Text(context.getString(R.string.choose_option)) },
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
                                Text(context.getString(R.string.close),color= Color.White)
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

        Row(modifier = Modifier
            .padding(8.dp)

        ) {

            Column( verticalArrangement = Arrangement.Center) {
                Icon(painter = painterResource(id = R.drawable.thermometer_half_24), contentDescription = "temperature Icon")
            }

            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(" Temperature")

            }

            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("F")

            }
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Switch(
                    modifier = Modifier
                        .padding(all = 3.dp)
                        .height(5.dp),
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
                    .padding(3.dp)
                    .align(Alignment.CenterVertically)

            ) {
                Text("C")

            }
        }

        HorizontalDivider()

    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(35.dp)
            .background(Blue300)
    ) {
        Text(text=context.getString(R.string.pref_custom_location_title),
            color=Color.White,
            style = QuickSandTypography.titleMedium,
            modifier = Modifier.padding(all = 8.dp)
        )
    }
    HorizontalDivider()


    Row (
        Modifier
            .padding(all = 8.dp)
            .clickable(onClick = { enteredCityPopup = true }),
    )

    {
        Column(verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.LocationOn, contentDescription = "city Icon")
        }

        Column {
            Text(context.getString(R.string.entered_city_name) + enteredCity.toString())
            if (enteredCityPopup) {
                CityDialog(
                    initialCity = enteredCity ?: "",
                    onCityChange = { newCity ->
                        scope.launch {
                            DataStoreManager.updateCityName(context, newCity)
                            enteredCityPopup = false
                        }
                    },
                    onDismiss = { enteredCityPopup = false }
                )
            }
        }
    }
    HorizontalDivider()
    // Weather Map menu item
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(enabled = navController != null && enteredCity!!.isNotBlank()) {
                navController?.navigate(R.id.weatherMapFragment)
            }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Weather Map Icon"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Weather Map",
            color = if (enteredCity?.isNotBlank() == true) Color.Unspecified else Color.Gray
        )
    }
    HorizontalDivider()
}

@Composable
fun CityDialog(initialCity: String, onCityChange: (String) -> Unit, onDismiss: () -> Unit) {
    var enteredCity by remember { mutableStateOf(initialCity) }
    val context = LocalContext.current

                AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = context.getString(R.string.select_city),
                style = QuickSandTypography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
                    text = {
            OutlinedTextField(
                value = enteredCity,
                onValueChange = { enteredCity = it },
                label = { Text(context.getString(R.string.entered_city_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                            )
                    },
                    confirmButton = {
            Button(onClick = {
                if (enteredCity.isNotBlank()) {
                        onCityChange(enteredCity.trim())
                    }
                onDismiss()
                },
                enabled = enteredCity.isNotBlank()
            ) {
                Text(context.getString(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(context.getString(R.string.cancel))
        }
    }
    )
}

