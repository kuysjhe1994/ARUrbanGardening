package com.arurbangarden.real.data.model

data class WeatherData(
    val temperature: Float,  // Celsius
    val humidity: Int,  // percentage
    val condition: String,  // e.g., "Clear", "Rain", "Cloudy"
    val conditionTagalog: String,
    val windSpeed: Float? = null,
    val rainfall: Float? = null,  // mm
    val timestamp: Long = System.currentTimeMillis()
)

data class WeatherRecommendation(
    val shouldWater: Boolean,
    val reason: String,
    val reasonTagalog: String,
    val nextWaterTime: Long? = null
)

