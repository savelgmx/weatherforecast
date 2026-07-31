package com.example.weatherforecast.utils

import android.content.Context
import com.example.weatherforecast.R

object MoonPhaseCalculator {

    fun calculateMoonPhase(context: Context, phase: Double): String {
        val wrongValue = context.resources.getString(R.string.wrong_value)
        val moonSymbols = context.resources.getStringArray(R.array.moon_symbols_array)
        val moonDescriptions = arrayOf(
            context.getString(R.string.new_moon),
            context.getString(R.string.waxing_crescent),
            context.getString(R.string.first_quarter),
            context.getString(R.string.waxing_gibbous),
            context.getString(R.string.full_moon),
            context.getString(R.string.waning_gibbous),
            context.getString(R.string.last_quarter),
            context.getString(R.string.waning_crescent),
            context.getString(R.string.new_moon)
        )

        if (phase < 0.0 || phase > 1.0) {
            return "Ungültiger Wert"
        }

        return when {
            //phase < 0.0 || phase > 1.0 -> wrongValue
            phase == 0.0 -> "${moonSymbols[0]} ${moonDescriptions[0]}"
            phase < 0.25 -> "${moonSymbols[1]} ${moonDescriptions[1]}"
            phase == 0.25 -> "${moonSymbols[2]} ${moonDescriptions[2]}"
            phase < 0.5 -> "${moonSymbols[3]} ${moonDescriptions[3]}"
            phase == 0.5 -> "${moonSymbols[4]} ${moonDescriptions[4]}"
            phase < 0.75 -> "${moonSymbols[5]} ${moonDescriptions[5]}"
            phase == 0.75 -> "${moonSymbols[6]} ${moonDescriptions[6]}"
            phase < 1.0 -> "${moonSymbols[7]} ${moonDescriptions[7]}"
            phase == 1.0 -> "${moonSymbols[8]} ${moonDescriptions[8]}"
            else -> wrongValue
        }
    }

    fun getMoonPhaseIconName(context: Context, phase: Double): Int {

        val moonIcons = arrayOf(
            R.drawable.moon_new,
            R.drawable.moon_waxing_crescent,
            R.drawable.moon_last_quarter,
            R.drawable.moon_waxing_gibbous,
            R.drawable.moon_full,
            R.drawable.moon_waning_gibbous,
            R.drawable.moon_first_quarter,
            R.drawable.moon_waning_crescent,
            R.drawable.moon_new
        )

        if (phase < 0.0 || phase > 1.0) {
            return R.drawable.moon_crescent
        }
        /*          val index = (phase * (moonIcons.size - 1)).toInt()
                    return moonIcons[index]*/
        return when {
            //phase < 0.0 || phase > 1.0 -> wrongValue
            phase == 0.0 -> moonIcons[0]
            phase < 0.25 -> moonIcons[1]
            phase == 0.25 -> moonIcons[2]
            phase < 0.5 -> moonIcons[3]
            phase == 0.5 -> moonIcons[4]
            phase < 0.75 -> moonIcons[5]
            phase == 0.75 -> moonIcons[6]
            phase < 1.0 -> moonIcons[7]
            phase == 1.0 -> moonIcons[8]
            else -> R.drawable.moon_crescent
        }
    }
}
