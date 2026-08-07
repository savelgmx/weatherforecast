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
    primary = Blue700,
    onPrimary = White,
    secondary = Color(0xFF03DAC5),
    onSecondary = Black,
    background = BackgroundDark,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = Black,
    error = ErrorDark,
    onError = White
)
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

