package com.arurbangarden.real.ui.tracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arurbangarden.real.databinding.ActivityGrowthTrackingBinding
import com.arurbangarden.real.data.model.GrowthRecord

class GrowthTrackingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGrowthTrackingBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrowthTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        // In production, load from database
        val records = emptyList<GrowthRecord>()
        
        if (records.isEmpty()) {
            binding.textEmpty.text = getString(com.arurbangarden.real.R.string.growth_tracking_no_records)
            binding.textEmpty.visibility = android.view.View.VISIBLE
        } else {
            binding.textEmpty.visibility = android.view.View.GONE
            // Set adapter with records
        }
        
        binding.btnRecord.setOnClickListener {
            // Launch camera to record growth
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

