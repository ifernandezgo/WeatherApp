package com.example.weatherapp.model

data class DailyForecast(
    val Date: String,
    val Temperature: FiveDayTemperature,
    val Day: DayNight,
    val Night: DayNight
)
