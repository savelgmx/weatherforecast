package com.example.weatherforecast.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*val Blue300 = Color(0xFF64B5F6)
val Blue400 = Color(0xFF42A5F5)
val Blue600 = Color(0xFF1976D2)
val Blue700 = Color(0xFF1565C0)
val Blue800 = Color(0xFF1565C0)
val Teal300 = Color(0xFF4DD0E1)
val Grey1 = Color(0xFFF5F5F5)
val White1 = Color(0xFFFFFFFF)
val White2 = Color(0xFFF5F6F5)
val Black1 = Color(0xFF22252D)
val Black2 = Color(0xFF2A2C35)
val RedErrorDark = Color(0xFFB00020)
val RedErrorLight = Color(0xFFEF5350)*/

val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    secondary = White1,
    onSecondary = Color.Black,
    background = Grey1,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = RedErrorLight,
    onError = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = Blue700,
    onPrimary = Color.White,
    secondary = Black1,
    onSecondary = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Black1,
    onSurface = Color.White,
    error = RedErrorDark,
    onError = Color.Black
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

