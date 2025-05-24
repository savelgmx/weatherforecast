package com.example.weatherforecast.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val Blue300 = Color(0xFF64B5F6)
val Blue400 = Color(0xFF42A5F5)
val Blue500 = Color(0xFF2196F3)
val Blue600 = Color(0xFF1E88E5)
val Blue700 = Color(0xFF1976D2)
val Blue800 = Color(0xFF1565C0)

val Teal300 = Color(0xFF1AC6FF)

val Grey1 = Color(0xFFF2F2F2)

val White1 = Color(0xFFF5)
val White2 = Color(0xf0f0e4)


val Black1 = Color(0xFF222222)
val Black2 = Color(0xFF000000)

val RedErrorDark = Color(0xFFB00020)
val RedErrorLight = Color(0xFFEF5350)

val LightColorPalette = lightColors(
    primary = Blue600,
    primaryVariant = Blue700,
    secondary = White1,
    background = Grey1,
    surface = White1,
    error = RedErrorLight,
    onPrimary = White1,
    onSecondary = Black1,
    onBackground = Black1,
    onSurface = Black1,
    onError = White1
)

val DarkColorPalette = darkColors(
    primary = Blue700,
    primaryVariant = Blue800,
    secondary = Black1,
    background = Black2,
    surface = Black1,
    error = RedErrorDark,
    onPrimary = White1,
    onSecondary = White1,
    onBackground = White1,
    onSurface = White1,
    onError = Black1
)