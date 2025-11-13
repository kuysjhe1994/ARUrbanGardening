package com.arurbangarden.real.ar

import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Session

data class MeasurementPoint(
    val anchor: Anchor,
    val worldPosition: FloatArray
)

class ARMeasurement(private val arCoreManager: ARCoreManager) {
    
    private var point1: MeasurementPoint? = null
    private var point2: MeasurementPoint? = null
    
    fun addMeasurementPoint(frame: Frame, hitResult: HitResult): Boolean {
        val anchor = arCoreManager.createAnchor(hitResult) ?: return false
        val pose = anchor.pose
        val position = floatArrayOf(pose.tx(), pose.ty(), pose.tz())
        
        val point = MeasurementPoint(anchor, position)
        
        when {
            point1 == null -> {
                point1 = point
                return true
            }
            point2 == null -> {
                point2 = point
                return true
            }
            else -> {
                // Reset and start new measurement
                reset()
                point1 = point
                return true
            }
        }
    }
    
    fun getDistance(): Float? {
        val p1 = point1 ?: return null
        val p2 = point2 ?: return null
        
        return arCoreManager.measureDistance(p1.worldPosition, p2.worldPosition)
    }
    
    fun getHeight(): Float? {
        // Height is the Y difference
        val p1 = point1 ?: return null
        val p2 = point2 ?: return null
        
        val height = kotlin.math.abs(p2.worldPosition[1] - p1.worldPosition[1]) * 100f
        return height
    }
    
    fun reset() {
        point1?.anchor?.detach()
        point2?.anchor?.detach()
        point1 = null
        point2 = null
    }
    
    fun hasTwoPoints(): Boolean {
        return point1 != null && point2 != null
    }
    
    fun hasOnePoint(): Boolean {
        return point1 != null && point2 == null
    }
}

