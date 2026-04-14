package com.example.lovebyte.data.network

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val weather: List<WeatherDescription>, // API can return multiple conditions
    val name: String,
    val main: MainWeather
)

data class MainWeather(
    val temp: Double
)

// describes the general weather condition (e.g., "rain", "clear")
data class WeatherDescription(
    val main: String,
    val description: String
)

// retrofit interface defining API endpoints
interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"
    ): WeatherResponse
}