package com.example.weatherforecast.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherforecast.presentation.viewmodels.SettingsViewModel
import com.example.weatherforecast.utils.WeatherLayer

/**
 * Step in the legend scale — a color + its label.
 *
 */
private data class LegendStep(val color: Color, val label: String)

/**
 * Color scale definition per layer type.
 */
private data class LegendScale(val title: String, val unit: String, val steps: List<LegendStep>)

/** Temperature scale (Visual Crossing typical raster colours). */
private val TEMP_SCALE = LegendScale(
    title = "Temperature",
    unit = "°C",
    steps = listOf(
        LegendStep(Color(0xFF2D00B4), "-20"),
        LegendStep(Color(0xFF4A00E0), "-15"),
        LegendStep(Color(0xFF0000FF), "-10"),
        LegendStep(Color(0xFF00BBFF), "-5"),
        LegendStep(Color(0xFF00E5FF), "0"),
        LegendStep(Color(0xFF00FF88), "5"),
        LegendStep(Color(0xFF55FF00), "10"),
        LegendStep(Color(0xFFAAFF00), "15"),
        LegendStep(Color(0xFFFFFF00), "20"),
        LegendStep(Color(0xFFFFCC00), "25"),
        LegendStep(Color(0xFFFF8800), "30"),
        LegendStep(Color(0xFFFF4400), "35"),
        LegendStep(Color(0xFFFF0000), "40"),
    )
)

/** Precipitation scale (mm/h). */
private val PRECIP_SCALE = LegendScale(
    title = "Precipitation",
    unit = "mm",
    steps = listOf(
        LegendStep(Color(0xFFFFFFFF), "0.0"),
        LegendStep(Color(0xFFBBFFBB), "0.5"),
        LegendStep(Color(0xFF88DD88), "1.0"),
        LegendStep(Color(0xFF44BB44), "2.5"),
        LegendStep(Color(0xFF009900), "5.0"),
        LegendStep(Color(0xFFCCCC00), "10"),
        LegendStep(Color(0xFFFFAA00), "25"),
        LegendStep(Color(0xFFFF6600), "50"),
        LegendStep(Color(0xFFFF0000), "100+"),
    )
)

/** Wind speed scale (km/h). */
private val WIND_SCALE = LegendScale(
    title = "Wind",
    unit = "km/h",
    steps = listOf(
        LegendStep(Color(0xFF88FF88), "0"),
        LegendStep(Color(0xFF44DD44), "10"),
        LegendStep(Color(0xFF00BB00), "20"),
        LegendStep(Color(0xFFBBBB00), "30"),
        LegendStep(Color(0xFFFFAA00), "40"),
        LegendStep(Color(0xFFFF6600), "50"),
        LegendStep(Color(0xFFFF3300), "60"),
        LegendStep(Color(0xFFFF0000), "80"),
        LegendStep(Color(0xFFCC00CC), "100+"),
    )
)

/** Map from WeatherLayer to scale definition. */
private fun scaleFor(layer: WeatherLayer): LegendScale = when (layer) {
    WeatherLayer.Temperature -> TEMP_SCALE
    WeatherLayer.Precipitation -> PRECIP_SCALE
    WeatherLayer.Wind -> WIND_SCALE
}

/**
 * A compact colour legend shown over the weather map.
 *
 * Displays a vertical gradient bar with labelled tick-marks,
 * sized so the bar height matches the label count × 14 dp.
 */
@Composable
fun WeatherLegend(
    layer: WeatherLayer,
    modifier: Modifier = Modifier,
    // Review item 5: unit preferences via the shared SettingsViewModel.
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val switchState by settingsViewModel.tempSwitch.collectAsStateWithLifecycle()
    val selectedWindOptions by settingsViewModel.windPref.collectAsStateWithLifecycle()

    val scale = scaleFor(layer)

    // Transform labels and unit based on user preferences
    val displayLabels = remember(layer, switchState, selectedWindOptions) {
        when (layer) {
            WeatherLayer.Temperature -> scale.steps.map { step ->
                tempLabelFor(step.label, switchState)
            }
            WeatherLayer.Wind -> scale.steps.map { step ->
                windLabelFor(step.label, selectedWindOptions)
            }
            else -> scale.steps.map { it.label }
        }
    }

    val displayUnit = remember(layer, switchState, selectedWindOptions) {
        when (layer) {
            WeatherLayer.Temperature -> if (switchState) "°C" else "°F"
            WeatherLayer.Wind -> when (selectedWindOptions) {
                0 -> "km/h"
                1 -> "m/s"
                2 -> "knots"
                3 -> "ft/s"
                else -> "km/h"
            }
            else -> scale.unit
        }
    }

    val stepCount = scale.steps.size
    // Each step gets 14 dp; total bar height is clamped to screen
    val barHeightDp = (stepCount * 14).coerceAtMost(240)

    Surface(
        modifier = modifier.width(80.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = 0.55f),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title + unit
            Text(
                text = scale.title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                maxLines = 1,
            )
            Text(
                text = displayUnit,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1,
            )

            Spacer(Modifier.height(4.dp))

            // Gradient bar + labels side-by-side
            Box(Modifier.fillMaxSize()) {
                // Labels on the LEFT, bar on the RIGHT
                Column(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    // Show a subset of labels (every 2nd step for readability)
                    scale.steps.forEachIndexed { i, step ->
                        val showLabel = i == 0 || i == stepCount - 1 || i % 2 == 0
                        val labelAlpha = when {
                            i == 0 || i == stepCount - 1 -> 1f
                            i % 2 == 0 -> 0.85f
                            else -> 0f
                        }
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(50.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (showLabel) {
                                val labelText = if (i < displayLabels.size) displayLabels[i] else step.label
                                Text(
                                    text = labelText,
                                    color = Color.White.copy(alpha = labelAlpha),
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Start,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }

                // Color bar
                Canvas(
                    modifier = Modifier
                        .width(12.dp)
                        .height(barHeightDp.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    val stepH = size.height / (stepCount - 1).toFloat()
                    for (i in 0 until stepCount - 1) {
                        drawRect(
                            color = scale.steps[i].color,
                            topLeft = Offset(0f, i * stepH),
                            size = Size(size.width, stepH)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Converts a Celsius label string to Celsius or Fahrenheit based on preference.
 */
private fun tempLabelFor(celsiusLabel: String, isCelsius: Boolean): String {
    val celsius = celsiusLabel.toIntOrNull() ?: return celsiusLabel
    return if (isCelsius) {
        celsiusLabel
    } else {
        "${(celsius * 9 / 5) + 32}"
    }
}

/**
 * Converts a km/h label string to the selected wind unit.
 */
private fun windLabelFor(kmhLabel: String, option: Int): String {
    if (option == 0) return kmhLabel
    val raw = kmhLabel.replace("+", "").toIntOrNull() ?: return kmhLabel
    val converted = when (option) {
        1 -> (raw / 3.6).toInt()   // km/h → m/s
        2 -> (raw * 0.587).toInt() // km/h → knots
        3 -> (raw * 0.91).toInt()  // km/h → ft/s
        else -> raw
    }
    val suffix = if (kmhLabel.endsWith("+")) "+" else ""
    return "$converted$suffix"
}