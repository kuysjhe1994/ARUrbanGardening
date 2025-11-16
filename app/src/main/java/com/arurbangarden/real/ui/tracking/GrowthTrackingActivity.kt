package com.arurbangarden.real.ui.tracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arurbangarden.real.R
import com.arurbangarden.real.data.database.GrowthDatabase
import com.arurbangarden.real.data.database.GrowthRepository
import com.arurbangarden.real.data.model.GrowthRecord
import com.arurbangarden.real.databinding.ActivityGrowthTrackingBinding
import kotlinx.coroutines.launch
import java.util.UUID

class GrowthTrackingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGrowthTrackingBinding
    private lateinit var repository: GrowthRepository
    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPlantId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrowthTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize database
        val database = GrowthDatabase.getDatabase(this)
        repository = GrowthRepository(database.growthDao())
        
        currentPlantId = intent.getStringExtra("plant_id")
        
        setupUI()
        loadRecords()
    }
    
    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        binding.btnRecord.setOnClickListener {
            captureGrowthPhoto()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnViewTimeline.setOnClickListener {
            // Show timeline view
            // In production, launch timeline activity
        }
    }
    
    private fun loadRecords() {
        lifecycleScope.launch {
            val plantId = currentPlantId ?: return@launch
            
            repository.getRecordsByPlant(plantId).collect { records ->
                if (records.isEmpty()) {
                    binding.textEmpty.text = getString(R.string.growth_tracking_no_records)
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.textEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    // Set adapter with records
                    // In production, create GrowthRecordAdapter
                }
            }
        }
    }
    
    private fun captureGrowthPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? android.graphics.Bitmap
            imageBitmap?.let {
                saveGrowthRecord(it)
            }
        }
    }
    
    private fun saveGrowthRecord(bitmap: android.graphics.Bitmap) {
        lifecycleScope.launch {
            // Save bitmap to file
            val photoPath = saveBitmapToFile(bitmap)
            
            // Create growth record
            val record = GrowthRecord(
                id = UUID.randomUUID().toString(),
                plantId = currentPlantId ?: "unknown",
                anchorId = null, // Will be set when AR anchor is created
                timestamp = System.currentTimeMillis(),
                photoPath = photoPath,
                metadata = com.arurbangarden.real.data.model.GrowthMetadata(
                    notes = "Growth photo"
                )
            )
            
            // Save to database
            repository.insertRecord(record)
            
            // Reload records
            loadRecords()
        }
    }
    
    private fun saveBitmapToFile(bitmap: android.graphics.Bitmap): String {
        return try {
            val fileName = "growth_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), fileName)
            
            val outputStream = java.io.FileOutputStream(file)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to internal storage
            val fileName = "growth_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(filesDir, fileName)
            
            val outputStream = java.io.FileOutputStream(file)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            
            file.absolutePath
        }
    }
}
