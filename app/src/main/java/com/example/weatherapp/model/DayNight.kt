package com.example.weatherapp.model

data class DayNight(
    val Icon: Int,
    val IconPhrase: String,
    val HasPrecipitation: Boolean,
    val PrecipitationType: String?,
    val PrecipitationIntensity: String?
)
