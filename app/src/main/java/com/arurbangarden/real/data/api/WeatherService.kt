package com.arurbangarden.real.data.api

import com.arurbangarden.real.data.model.WeatherData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherCondition>,
    val wind: WindData? = null,
    val rain: RainData? = null
)

data class MainData(
    val temp: Float,
    val humidity: Int,
    val feels_like: Float? = null
)

data class WeatherCondition(
    val main: String,
    val description: String
)

data class WindData(
    val speed: Float
)

data class RainData(
    val `1h`: Float? = null,
    val `3h`: Float? = null
)

class WeatherService(context: android.content.Context) {
    
    private val apiKey: String = try {
        context.resources.getString(
            context.resources.getIdentifier(
                "openweather_api_key",
                "string",
                context.packageName
            )
        )
    } catch (e: Exception) {
        "" // Fallback if API key not found
    }
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService = retrofit.create(WeatherApiService::class.java)
    
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherData? {
        if (apiKey.isEmpty()) return null
        return try {
            val response = apiService.getCurrentWeather(latitude, longitude, apiKey)
            convertToWeatherData(response)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun convertToWeatherData(response: WeatherResponse): WeatherData {
        val condition = response.weather.firstOrNull()?.main ?: "Unknown"
        val conditionTagalog = translateCondition(condition)
        
        return WeatherData(
            temperature = response.main.temp,
            humidity = response.main.humidity,
            condition = condition,
            conditionTagalog = conditionTagalog,
            windSpeed = response.wind?.speed,
            rainfall = response.rain?.`1h` ?: response.rain?.`3h`
        )
    }
    
    private fun translateCondition(condition: String): String {
        return when (condition.lowercase()) {
            "clear" -> "Malinaw"
            "clouds" -> "Maulap"
            "rain" -> "Ulan"
            "drizzle" -> "Ambun"
            "thunderstorm" -> "Bagyo"
            "snow" -> "Niyebe"
            "mist", "fog" -> "Hamog"
            else -> condition
        }
    }
}

