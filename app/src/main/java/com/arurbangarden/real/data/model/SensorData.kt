package com.arurbangarden.real.data.model

data class SensorData(
    val moisture: Int? = null,  // percentage (0-100)
    val ph: Float? = null,  // pH value (0-14)
    val temperature: Float? = null,  // Celsius
    val timestamp: Long = System.currentTimeMillis(),
    val sensorId: String? = null
)

data class BLESensorDevice(
    val name: String,
    val address: String,
    val isConnected: Boolean = false,
    val lastReading: SensorData? = null
)

