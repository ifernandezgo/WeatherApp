package com.example.weatherapp.model


class Repository(private val service: APIService) {
    suspend fun getLocationKey(apiKey: String, coordinates: String,
                               language: String, details: Boolean): Key {
        return service.getLocationKey(apiKey, coordinates, language, details)
    }

    suspend fun getCurrentConditions(locationKey: String, apiKey: String, language: String, details: Boolean): WeatherForecast {
        return service.getCurrentConditions(locationKey, apiKey, language, details).get(0)
    }

    suspend fun getFiveDayForecast(locationKey: String, apiKey: String, language: String, details: Boolean, metric: Boolean): FiveDayForecast {
        return service.getFiveDayForecast(locationKey, apiKey, language, details, metric)
    }
}