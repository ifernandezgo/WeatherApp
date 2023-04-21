package com.example.weatherapp.model

data class WeatherForecast(
    val LocalObservationDateTime: String,
    val WeatherText: String,
    val HasPrecipitation: Boolean,
    val WeatherIcon: Int,
    val PrecipitationType: String,
    val Temperature: Temperature
)
