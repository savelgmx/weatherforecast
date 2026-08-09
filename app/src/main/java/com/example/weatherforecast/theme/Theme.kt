package com.example.weatherforecast.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
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
    primary = Color(0xFF1565C0), // #1565C0 Blue800 — dark primary per spec token table (brand blue)
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

/**
 * Токены групп, отсутствующие в стандартном ColorScheme Material3.
 * drawerSurface/onDrawerSurface — отдельная группа (НЕ равна surface/onSurface):
 * тёмная тема — тёмно-серый фон/белый текст, светлая — белый/чёрный.
 * dialogTitleColor — исключение для заголовка dialogs (R.string.choose_option):
 * белый только в тёмной теме, в светлой — чёрный (тело/кнопки остаются чёрными в обеих).
 */
@Immutable
data class WeatherTokens(
    val drawerSurface: Color,
    val onDrawerSurface: Color,
    val dialogTitleColor: Color
)

private val LightWeatherTokens = WeatherTokens(
    drawerSurface = DrawerSurfaceLight,   // #FFFFFF
    onDrawerSurface = OnDrawerSurfaceLight, // #000000
    dialogTitleColor = Black
)

private val DarkWeatherTokens = WeatherTokens(
    drawerSurface = DrawerSurfaceDark,    // #1E1E1E тёмно-серый
    onDrawerSurface = OnDrawerSurfaceDark, // #FFFFFF
    dialogTitleColor = White
)

private val LocalWeatherTokens = staticCompositionLocalOf { LightWeatherTokens }

object WeatherTheme {
    val tokens: WeatherTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalWeatherTokens.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalWeatherTokens provides if (darkTheme) DarkWeatherTokens else LightWeatherTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = QuickSandTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

