package com.example.lovebyte.data.repository

import com.example.lovebyte.data.network.WeatherApi

class WeatherRepository(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeather(lat: Double, lon: Double, apiKey: String) =
        weatherApi.getCurrentWeather(lat, lon, apiKey)
}