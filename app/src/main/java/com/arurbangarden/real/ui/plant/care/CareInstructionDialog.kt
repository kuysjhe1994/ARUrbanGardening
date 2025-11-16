package com.arurbangarden.real.ui.plant.care

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.arurbangarden.real.R

/**
 * Dialog for showing care instructions with step-by-step guide
 * Kid-friendly: Max 2-3 steps, simple text
 */
class CareInstructionDialog(
    private val activity: android.app.Activity,
    private val action: CareAction,
    private val steps: List<String>,
    private val onComplete: (Boolean) -> Unit
) : DialogFragment() {
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_care_instructions, null)
        
        builder.setView(view)
        
        val dialog = builder.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        
        setupViews(view, dialog)
        
        return dialog
    }
    
    private fun setupViews(view: View, dialog: Dialog) {
        val titleText = view.findViewById<TextView>(R.id.textTitle)
        val stepsContainer = view.findViewById<LinearLayout>(R.id.containerSteps)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        
        // Set title
        titleText.text = when (action) {
            CareAction.WATER -> getString(R.string.water_instructions_title)
            CareAction.SUNLIGHT -> getString(R.string.sunlight_instructions_title)
            CareAction.SOIL -> getString(R.string.soil_instructions_title)
            CareAction.GROWTH -> getString(R.string.growth_instructions_title)
        }
        
        // Add steps
        steps.forEachIndexed { index, step ->
            val stepView = LayoutInflater.from(activity)
                .inflate(R.layout.item_care_step, stepsContainer, false)
            
            val stepNumber = stepView.findViewById<TextView>(R.id.textStepNumber)
            val stepText = stepView.findViewById<TextView>(R.id.textStepText)
            
            stepNumber.text = "${index + 1}"
            stepText.text = step
            
            stepsContainer.addView(stepView)
        }
        
        // Complete button
        btnComplete.setOnClickListener {
            onComplete(true)
            dialog.dismiss()
        }
        
        // Cancel button
        btnCancel.setOnClickListener {
            onComplete(false)
            dialog.dismiss()
        }
    }
}

// Non-fragment version for Activity
class CareInstructionDialogActivity(
    private val activity: android.app.Activity,
    private val action: CareAction,
    private val steps: List<String>,
    private val onComplete: (Boolean) -> Unit
) {
    
    private var dialog: AlertDialog? = null
    
    fun show() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_care_instructions, null)
        
        builder.setView(view)
        
        dialog = builder.create()
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.show()
        
        setupViews(view)
    }
    
    private fun setupViews(view: View) {
        val titleText = view.findViewById<TextView>(R.id.textTitle)
        val stepsContainer = view.findViewById<LinearLayout>(R.id.containerSteps)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        
        // Set title
        titleText.text = when (action) {
            CareAction.WATER -> activity.getString(R.string.water_instructions_title)
            CareAction.SUNLIGHT -> activity.getString(R.string.sunlight_instructions_title)
            CareAction.SOIL -> activity.getString(R.string.soil_instructions_title)
            CareAction.GROWTH -> activity.getString(R.string.growth_instructions_title)
        }
        
        // Clear existing steps
        stepsContainer.removeAllViews()
        
        // Add steps
        steps.forEachIndexed { index, step ->
            val stepView = LayoutInflater.from(activity)
                .inflate(R.layout.item_care_step, stepsContainer, false)
            
            val stepNumber = stepView.findViewById<TextView>(R.id.textStepNumber)
            val stepText = stepView.findViewById<TextView>(R.id.textStepText)
            
            stepNumber.text = "${index + 1}"
            stepText.text = step
            
            stepsContainer.addView(stepView)
        }
        
        // Complete button
        btnComplete.setOnClickListener {
            onComplete(true)
            dialog?.dismiss()
        }
        
        // Cancel button
        btnCancel.setOnClickListener {
            onComplete(false)
            dialog?.dismiss()
        }
    }
}

