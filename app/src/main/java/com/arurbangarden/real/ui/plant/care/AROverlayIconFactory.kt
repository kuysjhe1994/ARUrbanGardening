package com.arurbangarden.real.ui.plant.care

import android.content.Context
import androidx.core.content.ContextCompat
import com.arurbangarden.real.R
import com.arurbangarden.real.data.model.Plant

/**
 * Factory for creating AR overlay icons
 */
class AROverlayIconFactory(private val context: Context, private val plant: Plant) {
    
    fun createWaterOverlay(): AROverlayIcon {
        return AROverlayIcon(context).apply {
            setIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_water),
                Color.parseColor("#4A90E2") // Blue
            )
            setLabel(context.getString(R.string.water_label))
            
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
    
    fun createSunlightOverlay(): AROverlayIcon {
        return AROverlayIcon(context).apply {
            setIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_sunlight),
                Color.parseColor("#F5A623") // Orange/Yellow
            )
            setLabel(context.getString(R.string.sunlight_label))
            
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
    
    fun createSoilOverlay(): AROverlayIcon {
        return AROverlayIcon(context).apply {
            setIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_soil),
                Color.parseColor("#8B4513") // Brown
            )
            setLabel(context.getString(R.string.soil_label))
            
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
    
    fun createGrowthOverlay(): AROverlayIcon {
        return AROverlayIcon(context).apply {
            setIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_growth),
                Color.parseColor("#7ED321") // Green
            )
            setLabel(context.getString(R.string.growth_label))
            
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}

