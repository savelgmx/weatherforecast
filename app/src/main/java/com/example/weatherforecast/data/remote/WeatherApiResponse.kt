package com.example.weatherforecast.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    @SerializedName("queryCost") val queryCost: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("resolvedAddress") val resolvedAddress: String,
    @SerializedName("address") val address: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("tzoffset") val tzOffset: Double,
    @SerializedName("days") val days: List<ApiDay>
    )

data class ApiDay(
    @SerializedName("datetime") val date: String,
    @SerializedName("datetimeEpoch") val dateEpoch: Long,
    @SerializedName("tempmax") val tempMax: Double,
    @SerializedName("tempmin") val tempMin: Double,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feelslikemax") val feelsLikeMax: Double,
    @SerializedName("feelslikemin") val feelsLikeMin: Double,
    @SerializedName("feelslike") val feelsLike: Double,
    @SerializedName("dew") val dew: Double,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("precip") val precipitation: Double,
    @SerializedName("precipprob") val precipProbability: Double,
    @SerializedName("precipcover") val precipCover: Double,
    @SerializedName("preciptype") val precipType: List<String>?,
    @SerializedName("snow") val snow: Double,
    @SerializedName("snowdepth") val snowDepth: Double,
    @SerializedName("windgust") val windGust: Double,
    @SerializedName("windspeed") val windSpeed: Double,
    @SerializedName("winddir") val windDir: Double,
    @SerializedName("pressure") val pressure: Double,
    @SerializedName("cloudcover") val cloudCover: Double,
    @SerializedName("visibility") val visibility: Double,
    @SerializedName("solarradiation") val solarRadiation: Double,
    @SerializedName("solarenergy") val solarEnergy: Double,
    @SerializedName("uvindex") val uvIndex: Int,
    @SerializedName("severerisk") val severeRisk: Int,
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunriseEpoch") val sunriseEpoch: Long,
    @SerializedName("sunset") val sunset: String,
    @SerializedName("sunsetEpoch") val sunsetEpoch: Long,
    @SerializedName("moonphase") val moonPhase: Double,
    @SerializedName("conditions") val conditions: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("hours") val hours: List<ApiHour>?
)

data class ApiHour(
    @SerializedName("datetime") val time: String,
    @SerializedName("datetimeEpoch") val timeEpoch: Long,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feelslike") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("dew") val dew: Double,
    @SerializedName("precip") val precipitation: Double,
    @SerializedName("precipprob") val precipProbability: Double,
    @SerializedName("snow") val snow: Double,
    @SerializedName("snowdepth") val snowDepth: Double,
    @SerializedName("preciptype") val precipType: List<String>?,
    @SerializedName("windgust") val windGust: Double,
    @SerializedName("windspeed") val windSpeed: Double,
    @SerializedName("winddir") val windDir: Double,
    @SerializedName("pressure") val pressure: Double,
    @SerializedName("visibility") val visibility: Double,
    @SerializedName("cloudcover") val cloudCover: Double,
    @SerializedName("solarradiation") val solarRadiation: Double,
    @SerializedName("solarenergy") val solarEnergy: Double,
    @SerializedName("uvindex") val uvIndex: Int,
    @SerializedName("severerisk") val severeRisk: Int,
    @SerializedName("conditions") val conditions: String,
    @SerializedName("icon") val icon: String,
)

