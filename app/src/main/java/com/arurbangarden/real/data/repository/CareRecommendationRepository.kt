package com.arurbangarden.real.data.repository

import com.arurbangarden.real.data.model.*
import com.arurbangarden.real.data.sensor.BLESensorManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CareRecommendationRepository(
    private val weatherService: com.arurbangarden.real.data.api.WeatherService,
    private val sensorManager: BLESensorManager
) {
    
    /**
     * Generate care recommendations based on plant, weather, and sensor data
     */
    suspend fun getCareRecommendation(
        plant: Plant,
        latitude: Double? = null,
        longitude: Double? = null
    ): CareRecommendation {
        val actions = mutableListOf<CareAction>()
        
        // Get weather data
        val weatherData = if (latitude != null && longitude != null) {
            weatherService.getCurrentWeather(latitude, longitude)
        } else {
            null
        }
        
        // Get sensor data
        val sensorData = sensorManager.sensorData.value
        
        // Water recommendation
        val waterAction = determineWaterAction(plant, weatherData, sensorData)
        actions.add(waterAction)
        
        // Sunlight recommendation
        val sunlightAction = determineSunlightAction(plant, weatherData)
        actions.add(sunlightAction)
        
        // Other care actions based on plant needs
        if (plant.careTips.isNotEmpty()) {
            // Add trim/fertilize recommendations if needed
        }
        
        return CareRecommendation(
            plantId = plant.id,
            actions = actions.sortedByDescending { it.priority }
        )
    }
    
    private fun determineWaterAction(
        plant: Plant,
        weather: WeatherData?,
        sensor: SensorData?
    ): CareAction {
        val needsWater = when {
            // Sensor says dry
            sensor?.moisture != null && sensor.moisture < 30 -> true
            // Weather shows no rain and plant needs frequent watering
            weather != null && weather.rainfall == null && 
            plant.waterFrequency == WaterFrequency.DAILY -> true
            // Default based on plant needs
            else -> false
        }
        
        return CareAction(
            type = CareActionType.WATER,
            status = if (needsWater) CareStatus.NEEDED_NOW else CareStatus.OK,
            message = if (needsWater) "Water now" else "Water OK",
            messageTagalog = if (needsWater) "Diligan ngayon" else "Okay ang tubig",
            iconRes = android.R.drawable.ic_menu_compass, // Replace with custom icon
            priority = if (needsWater) 10 else 1
        )
    }
    
    private fun determineSunlightAction(
        plant: Plant,
        weather: WeatherData?
    ): CareAction {
        // Simplified logic - in production, use ambient light sensor
        val status = when (plant.sunlightNeeds) {
            SunlightNeeds.FULL_SUN -> CareStatus.OK
            SunlightNeeds.PARTIAL_SUN -> CareStatus.OK
            SunlightNeeds.SHADE -> CareStatus.OK
        }
        
        return CareAction(
            type = CareActionType.SUNLIGHT,
            status = status,
            message = "Sunlight OK",
            messageTagalog = "Tamang araw",
            iconRes = android.R.drawable.ic_menu_compass, // Replace with custom icon
            priority = 5
        )
    }
}

