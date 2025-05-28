package com.example.weatherforecast.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherforecast.R

val QuickSand = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_bold, FontWeight.Bold),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_light, FontWeight.Light)
)

/*
h1 (30.sp, Bold): Closest to headlineLarge (32.sp), used for prominent text like temperature. Assign to headlineLarge with custom size (30.sp).
h5 (24.sp, Medium): Matches headlineSmall (24.sp), used for city name and day. Assign to headlineSmall.
h6 (20.sp, Medium): Closest to titleLarge (22.sp), used for secondary text like feels like, wind, pressure. Assign to titleLarge.
subtitle1 (16.sp, Normal): Matches titleMedium (16.sp), used for headers. Assign to titleMedium.
body1 (16.sp, Normal): Matches bodyLarge (16.sp), used for error/loading messages. Assign to bodyLarge.
body2 (14.sp, Normal): Matches bodyMedium (14.sp), used for smaller text. Assign to bodyMedium.
*/
val QuickSandTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)