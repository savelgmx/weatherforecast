package com.example.weatherforecast.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = White,
    secondary = Color(0xFF03DAC5), // You can adjust this to your preferred secondary color
    onSecondary = Black,
    background = Blue600,
    onBackground = White,
    surface = SurfaceLight,
    onSurface = Black,
    error = ErrorLight,
    onError = White
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1565C0), // Using Blue700 for primary in dark theme for contrast
    onPrimary = White,
    secondary = Color(0xFF03DAC5),
    onSecondary = Black,
    background = Blue600, // Keeping Blue600 for consistency, though a darker shade could be used
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    error = ErrorDark,
    onError = White
)
/*
Implementation Details
Background Color: Setting background = Blue600 ensures that the main background,
including the scaffold if it uses the theme's background, is blue. If in MainScreen.kt,
the Scaffold has a specific backgroundColor, ensure it's set to MaterialTheme.colorScheme.background or remove the override to use the theme.
Text Color: By setting onBackground = Color.White, any text using MaterialTheme.colorScheme.onBackground, like in WeatherHeader, will be white, resolving the issue of black text on a blue background.
Theme Consistency: For light theme, using Blue 600 as background might make it look like a colored theme rather than a standard light theme, but since you specified this color, it's acceptable. For dark theme, Blue 600 is dark enough, so keeping it consistent is fine, though you could consider a darker shade like Blue 700 (0xFF1565C0) for background in dark theme if preferred.
Other Color Roles: We set surface to white for light theme and a dark gray for dark theme, ensuring cards and elevated surfaces have appropriate backgrounds. error and onError are set to standard Material 3 values for consistency, with onError = White for both to ensure readability on error colors.
Comparative Analysis
To organize the options, consider the following table comparing the current and proposed settings:
*/


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = QuickSandTypography,
        shapes = AppShapes,
        content = content
    )
}

