package com.arurbangarden.real.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Ambient Light Sensor - Real sensor data for sunlight advice
 * Uses Android's light sensor to get actual ambient light level
 */
class AmbientLightSensor(
    private val context: Context,
    private val onLightLevelChanged: (Float) -> Unit
) : SensorEventListener {
    
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var isListening = false
    
    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
    }
    
    fun start() {
        if (isListening || lightSensor == null) return
        
        sensorManager?.registerListener(
            this,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        
        isListening = true
    }
    
    fun stop() {
        if (!isListening) return
        
        sensorManager?.unregisterListener(this)
        isListening = false
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightLevel = event.values[0] // lux
            onLightLevelChanged(lightLevel)
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
    
    fun isAvailable(): Boolean {
        return lightSensor != null
    }
    
    fun getCurrentLightLevel(): Float? {
        return if (isListening) {
            // Return last known value
            // In production, store last value in a variable
            null
        } else {
            null
        }
    }
}

