package com.arurbangarden.real.ui.plant.care

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.arurbangarden.real.R

/**
 * "Good job!" badge that appears after completing a care action
 */
class GoodJobBadge(context: Context, private val action: CareAction) : FrameLayout(context) {
    
    init {
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        
        setPadding(48, 48, 48, 48)
        setBackgroundResource(R.drawable.bg_good_job_badge)
        
        setupViews()
    }
    
    private fun setupViews() {
        // Badge icon
        val iconView = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = 16
            }
            
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_badge))
        }
        addView(iconView)
        
        // "Good job!" text
        val textView = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
            
            text = context.getString(R.string.good_job)
            textSize = 32f
            setTextColor(Color.YELLOW)
            gravity = Gravity.CENTER
        }
        addView(textView)
    }
}

