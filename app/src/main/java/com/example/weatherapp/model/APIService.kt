package com.example.weatherapp.model

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @GET("locations/v1/cities/geoposition/search")
    suspend fun getLocationKey(
        @Query("apikey") apiKey: String,
        @Query("q") coordinates: String,
        @Query("language") language: String,
        @Query("details") details: Boolean
    ): Key

    @GET("currentconditions/v1/{locationKey}")
    suspend fun getCurrentConditions(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("language") language: String,
        @Query("details") details: Boolean
    ): List<WeatherForecast>

    @GET("forecasts/v1/daily/5day/{locationKey}")
    suspend fun getFiveDayForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("language") language: String,
        @Query("details") details: Boolean,
        @Query("metric") metric: Boolean
    ): FiveDayForecast
}