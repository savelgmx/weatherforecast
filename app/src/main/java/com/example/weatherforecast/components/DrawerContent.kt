package com.example.weatherforecast.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.presentation.viewmodels.SettingsViewModel
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.WeatherComposables
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navController: NavController? = null,
    // Review item 5: preferences are read through the shared SettingsViewModel
    // (Activity-scoped via hiltViewModel) instead of the static DataStoreManager.
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val switchState by settingsViewModel.tempSwitch.collectAsStateWithLifecycle()

    val selectedWindOption by settingsViewModel.windPref.collectAsStateWithLifecycle()
    var windSpeedUnitsToSelect= context.resources.getStringArray(R.array.wind_speed_units) //arrayOf("km/h", "m/s", "knots", "ft/s")
    var windSpeedUnitsPopup by remember { mutableStateOf(false) }

    val selectedPressureOption by settingsViewModel.pressurePref.collectAsStateWithLifecycle()
    var pressureUnitsToSelect= context.resources.getStringArray(R.array.pressure_units)//arrayOf("mm Hg", "inches Hg", "hPa", "mbar")
    var pressureUnitsPopup by remember { mutableStateOf(false) }

    val enteredCity by settingsViewModel.cityName.collectAsStateWithLifecycle()//text field with entered city name
    var enteredCityPopup by remember { mutableStateOf(false) }

    // ⚠ Bug #2 fix: весь контент drawer'а внутри одного Column.
    // Ранее Column закрывался преждевременно — секции "Custom location"
    // и "Weather Map" оказывались снаружи, нарушая вёрстку и наследование цветов.

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
                .fillMaxWidth()
                .padding(all = 8.dp)
                .clickable(onClick = { windSpeedUnitsPopup = true }),
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
                                            settingsViewModel.updateWindPref(option)
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
            Column { Text(" "+WeatherComposables.selectionWindSignature(selectedWindOption)) }
        }

        HorizontalDivider()

        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .clickable(onClick = { pressureUnitsPopup = true }),
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
                                            settingsViewModel.updatePressurePref(option)
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
                Text(" "+ WeatherComposables.selectionPressureSignature(selectedPressureOption))
            }
        }
        HorizontalDivider()

        Row(modifier = Modifier
            .fillMaxWidth()
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
                            settingsViewModel.updateTempSwitch(isChecked)
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
            .fillMaxWidth()
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
                CitySelectionDialog(
                    onCitySelected = { cityName ->
                        scope.launch {
                            // IMPORTANT: updateCityName must be called FIRST.
                            // addRecentCity writes RECENT_CITIES_KEY to DataStore,
                            // which triggers a data emission. If the city hasn't
                            // been saved to LOCATED_CITY_NAME_KEY yet, cityNamePrefFlow
                            // re-emits the old (null/blank) value — causing the
                            // ViewModel observers to restart auto-detect or show
                            // the dialog. This ordering matches selectCity() in
                            // OpenWeatherForecastViewModel.
                            settingsViewModel.updateCityName(cityName)
                            settingsViewModel.addRecentCity(cityName)
                            enteredCityPopup = false
                        }
                    },
                    onDismiss = { enteredCityPopup = false }
                )
            }
        }
    }
    HorizontalDivider()
        // Weather Map menu item — безопасная навигация с проверкой enteredCity
        // ⚠ Bug #3 fix: в navigate передаётся "$enteredCity" через интерполяцию.
        // Ранее было .navigate("weatherMap") без города, из-за чего
        // MainActivity.kt получал пустую строку и карта не центрировалась.
        // enteredCity берётся из DataStoreManager.cityNamePrefFlow (строка 58).
    Row(
        Modifier
            .fillMaxWidth()
                .clickable(
                    enabled = navController != null && !enteredCity.isNullOrBlank()
                ) {
                    // только навигация, если город не null и не пустой
                navController?.navigate("weatherMap/$enteredCity")
            }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Weather Map Icon"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = context.getString(R.string.weather_map),
                color = if (!enteredCity.isNullOrBlank()) Color.Unspecified else Color.Gray
        )
    }
    HorizontalDivider()
    }
}