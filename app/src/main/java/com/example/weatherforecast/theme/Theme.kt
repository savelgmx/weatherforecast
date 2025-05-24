package com.example.weatherforecast.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val LightThemeColors = lightColors(
    primary = Blue600,
    primaryVariant = Blue700,
    secondary = Color.White,
    background = Grey1,
    surface = Color.White,
    error = RedErrorLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

val DarkThemeColors = darkColors(
    primary = Blue700,
    primaryVariant = Blue800,
    secondary = Black1,
    background = Color.Black,
    surface = Black1,
    error = RedErrorDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    displayProgressBar: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkThemeColors
    } else {
        LightThemeColors
    }

    MaterialTheme(
        colors = colors,
        typography = QuickSandTypography,
        shapes = AppShapes,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
            ){
                content()
            }


        }
    )
}

