package com.arurbangarden.real.ui.plant.care

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.arurbangarden.real.R

/**
 * AR Overlay Icon - Animated, colorful, kid-friendly icon for AR care guide
 */
class AROverlayIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var iconImageView: ImageView
    private lateinit var labelTextView: TextView
    private lateinit var adviceTextView: TextView
    
    private var iconDrawable: Drawable? = null
    private var iconColor: Int = Color.BLUE
    private var isCompleted = false
    private var adviceText: String? = null
    
    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)
        setBackgroundResource(R.drawable.bg_overlay_icon)
        
        setupViews()
    }
    
    private fun setupViews() {
        // Icon
        iconImageView = ImageView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                width = 120
                height = 120
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        addView(iconImageView)
        
        // Label
        labelTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
        }
        addView(labelTextView)
        
        // Advice text (optional, shown when sensor data is available)
        adviceTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            textSize = 14f
            setTextColor(Color.YELLOW)
            gravity = android.view.Gravity.CENTER
            visibility = View.GONE
        }
        addView(adviceTextView)
    }
    
    fun setIcon(drawable: Drawable?, color: Int = Color.BLUE) {
        iconDrawable = drawable
        iconColor = color
        
        drawable?.let {
            iconImageView.setImageDrawable(it)
            iconImageView.setColorFilter(color)
        }
    }
    
    fun setLabel(text: String) {
        labelTextView.text = text
    }
    
    fun setCompleted(completed: Boolean) {
        isCompleted = completed
        
        if (completed) {
            setBackgroundResource(R.drawable.bg_overlay_icon_completed)
            iconImageView.setColorFilter(Color.GREEN)
            labelTextView.setTextColor(Color.GREEN)
        } else {
            setBackgroundResource(R.drawable.bg_overlay_icon)
            iconImageView.setColorFilter(iconColor)
            labelTextView.setTextColor(Color.WHITE)
        }
        
        invalidate()
    }
    
    fun updateAdvice(advice: String) {
        adviceText = advice
        adviceTextView.text = advice
        adviceTextView.visibility = View.VISIBLE
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        // Draw glow effect when completed
        if (isCompleted) {
            val paint = Paint().apply {
                color = Color.GREEN
                style = Paint.Style.STROKE
                strokeWidth = 8f
                alpha = 128
            }
            canvas?.drawCircle(
                width / 2f,
                height / 2f,
                width / 2f - 4,
                paint
            )
        }
    }
}

