package com.arurbangarden.real.ar

import android.content.Context
import android.view.MotionEvent
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ARCoreManager(private val context: Context) {
    
    private var session: Session? = null
    private val _arState = MutableStateFlow<ARState>(ARState.NotInitialized)
    val arState: StateFlow<ARState> = _arState
    
    private val anchors = mutableListOf<Anchor>()
    
    fun initialize(): Boolean {
        return try {
            // Check if ARCore is supported
            val availability = ArCoreApk.getInstance().checkAvailability(context)
            if (availability.isTransient) {
                // ARCore is downloading, wait
                _arState.value = ARState.Downloading
                return false
            }
            
            if (availability != ArCoreApk.Availability.SUPPORTED_INSTALLED) {
                _arState.value = ARState.NotSupported
                return false
            }
            
            // Create session
            session = Session(context)
            _arState.value = ARState.Ready
            true
        } catch (e: Exception) {
            _arState.value = ARState.Error(e.message ?: "Unknown error")
            false
        }
    }
    
    fun resume() {
        session?.resume()
    }
    
    fun pause() {
        session?.pause()
    }
    
    fun getSession(): Session? = session
    
    /**
     * Perform hit test to find planes/points in real world
     */
    fun hitTest(frame: Frame, x: Float, y: Float): List<HitResult> {
        return try {
            frame.hitTest(x, y)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Create anchor at hit result
     */
    fun createAnchor(hitResult: HitResult): Anchor? {
        return try {
            val anchor = hitResult.createAnchor()
            anchors.add(anchor)
            anchor
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Measure distance between two points using ARCore depth
     */
    fun measureDistance(point1: FloatArray, point2: FloatArray): Float {
        val dx = point2[0] - point1[0]
        val dy = point2[1] - point1[1]
        val dz = point2[2] - point1[2]
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz) * 100f  // Convert to cm
    }
    
    /**
     * Get detected planes
     */
    fun getDetectedPlanes(frame: Frame): List<Plane> {
        return try {
            frame.acquireUpdatedTrackables(Plane::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun release() {
        anchors.forEach { it.detach() }
        anchors.clear()
        session?.close()
        session = null
    }
}

sealed class ARState {
    object NotInitialized : ARState()
    object Downloading : ARState()
    object NotSupported : ARState()
    object Ready : ARState()
    data class Error(val message: String) : ARState()
}

