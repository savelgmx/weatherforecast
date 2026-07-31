package com.example.weatherforecast.utils

import android.content.Context
import com.example.weatherforecast.R
import com.example.weatherforecast.domain.models.HourlyWeather
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object WeatherFormatter {

    /**
     * Filters and returns the next 24 hours of forecast for a given city timezone.
     *
     * @param hourlyList Full list of hourly forecasts.
     * @param timezone IANA timezone string (e.g., "Europe/Berlin").
     * @param startEpochSeconds Optional start time (epoch seconds UTC).
     *                          Defaults to "now" in the city timezone.
     * @return List of at most 24 items, sorted by dt ascending.
     */
    fun filterNext24Hours(
        hourlyList: List<HourlyWeather>,
        timezone: String,
        startEpochSeconds: Long? = null
    ): List<HourlyWeather> {
        val zoneId = try {
            ZoneId.of(timezone)
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }

        // Start: now in city zone, or the provided epoch
        val startDateTime = if (startEpochSeconds != null) {
            Instant.ofEpochSecond(startEpochSeconds).atZone(zoneId)
        } else {
            ZonedDateTime.now(zoneId)
        }

        val endDateTime = startDateTime.plusHours(24)

        return hourlyList
            .filter { hour ->
                val hourTime = Instant.ofEpochSecond(hour.dt.toLong()).atZone(zoneId)
                !hourTime.isBefore(startDateTime) && hourTime.isBefore(endDateTime)
            }
            .sortedBy { it.dt }
            .take(24)
    }


    fun updateTemperature(temperature: Int, switchState: Boolean): String {
        val unitAbbreviation = if (switchState) "C° " else "F° "

        val temp = if (switchState) {
            "$temperature$unitAbbreviation"
        } else {
            val fahrenheitTemp = (temperature * 9 / 5) + 32
            "$fahrenheitTemp$unitAbbreviation"
        }
        return temp
    }

    fun convertWindSpeed(windSpeed: Int, selectedWindOptions: Int): String {
        // Convert the wind speed to the selected unit using integer calculations
        val convertedWindSpeed = when (selectedWindOptions) {
            0 -> windSpeed  // km/h is the default unit
            1 -> (windSpeed / 3.6).toInt() // Conversion from km/h to m/s
            2 -> (windSpeed * 0.587).toInt() // Conversion from km/h to knots
            3 -> (windSpeed * 0.91).toInt()  // Conversion from km/h to ft/s
            else -> {}
        }
        return convertedWindSpeed.toString()
    }


    fun updateDateToToday(dt: Int?): String {
        //API returns date/time as a UnixEpoc integer timestamp
        //we must transform this with datetime format

        val simpleDateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault())
        var today: String = "Today"
        if (dt != null) {
            today = simpleDateFormat.format(dt * 1000L)
        }

        return today
    }

    // Existing updateTime() kept for sunrise/sunset

    /**
     * Formats an hourly forecast timestamp (epoch seconds) into "HH:mm"
     * using the forecast city timezone.
     */
    fun formatHour(epochSeconds: Long, timezone: String): String {
        val zoneId = try {
            ZoneId.of(timezone)
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }

        val instant = Instant.ofEpochSecond(epochSeconds)
        val zonedDateTime = instant.atZone(zoneId)

        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    /**
     * Converts an epoch timestamp (seconds) into "HH:mm" string,
     * formatted in the given city's timezone.
     *
     * @param epochSeconds Epoch timestamp in seconds (API gives sunrise/sunset).
     * @param timezone IANA timezone string from API (e.g. "Europe/Berlin").
     * @return formatted time string in "HH:mm", or "--:--" if invalid.
     */
    fun updateTime(epochSeconds: Int?, timezone: String): String {
        if (epochSeconds == null) return "--:--"

        val zoneId = try {
            ZoneId.of(timezone)
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }

        val instant = Instant.ofEpochSecond(epochSeconds.toLong())
        val zonedDateTime = instant.atZone(zoneId)

        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    fun updateUVLevel(context: Context, uvLevel: Int): String {

        val UVDescriptions = context.resources.getStringArray(R.array.uv_index_values)
        // Check for a valid index
        if (uvLevel < 0) {
            return "Invalid unit index"
        }
        //now choose right uv value from array
        return when {
            uvLevel <= 2 -> UVDescriptions[0] // low 2 or less
            uvLevel in (3..5) -> UVDescriptions[1]//average 3-5
            uvLevel in (6..7) -> UVDescriptions[2]//high 6-7
            uvLevel in (8..10) -> UVDescriptions[3]//very high 8-10
            uvLevel >= 11 -> UVDescriptions[4]//extreme 11 and higher
            else -> {
                context.resources.getString(R.string.wrong_value)
            }
        }
    }

    fun calculateDawnAndDusk(sunrise: Int?, sunset: Int?): Array<String?> {
        // Constants for dawn and dusk offsets (30 minutes before and after) in seconds
        val dawnOffset = -30 * 60  // 30 minutes BEFORE sunrise in seconds
        val duskOffset = 30 * 60   // 30 minutes AFTER sunset in seconds

        // Calculate dawn and dusk times in seconds
        val dawnTime = sunrise?.plus(dawnOffset)
        val duskTime = sunset?.plus(duskOffset)

        // Convert to milliseconds for formatting
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dawnString = dawnTime?.let { Date(it * 1000L) }?.let { dateFormat.format(it) }
        val duskString = duskTime?.let { Date(it * 1000L) }?.let { dateFormat.format(it) }

        return arrayOf(dawnString, duskString)
    }
    /**
     * Calculates total day duration, elapsed day time and sun progress
     * for SunriseSunsetArcCard.
     *
     * @param sunrise Sunrise time in "HH:mm" format (local to city).
     * @param sunset Sunset time in "HH:mm" format (local to city).
     * @param timezone IANA timezone string from API (e.g. "Europe/Berlin").
     * @return arrayOf(dayDuration:String, elapsedDayTime:String, progress:Float as String)
     */
    fun calculateDayDurationElapsedDayTimeAndSunIconProgress(
        sunrise: String,
        sunset: String,
        timezone: String
    ): Array<String> {
        return try {
            val zoneId = try {
                ZoneId.of(timezone)   // e.g. "Europe/Berlin"
            } catch (e: Exception) {
                ZoneId.systemDefault() // fallback if invalid zone
            }

            val today = LocalDate.now(zoneId)

            val sunriseTime = LocalTime.parse(sunrise)
            val sunsetTime = LocalTime.parse(sunset)

            val sunriseDateTime = ZonedDateTime.of(today, sunriseTime, zoneId)
            val sunsetDateTime = ZonedDateTime.of(today, sunsetTime, zoneId)

            val nowCity = ZonedDateTime.now(zoneId)

            // --- Day duration ---
            var totalMinutes =
                Duration.between(sunriseDateTime, sunsetDateTime).toMinutes().toInt()
            if (totalMinutes < 0) totalMinutes = 0   // defensive

            // --- Elapsed time since sunrise ---
            var elapsedMinutes = Duration.between(sunriseDateTime, nowCity).toMinutes().toInt()
            if (elapsedMinutes < 0) elapsedMinutes = 0
            if (elapsedMinutes > totalMinutes) elapsedMinutes = totalMinutes

            // --- Progress (0..1) ---
            val progress = if (totalMinutes > 0) {
                elapsedMinutes.toFloat() / totalMinutes.toFloat()
            } else {
                0f
            }

            // --- Format results ---
            val dayHours = totalMinutes / 60
            val dayMinutes = totalMinutes % 60
            val dayDuration = String.format("%d:%02d", dayHours, dayMinutes)

            val elapsedHours = elapsedMinutes / 60
            val elapsedMins = elapsedMinutes % 60
            val elapsedDayTime = String.format("%d:%02d", elapsedHours, elapsedMins)

            arrayOf(dayDuration, elapsedDayTime, progress.toString())
        } catch (e: Exception) {
            // Fallback in case of parsing/zone error
            arrayOf("0:00", "0:00", "0.0")
        }
    }
}
