package com.arurbangarden.real.ui.tracking

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arurbangarden.real.R
import com.arurbangarden.real.data.database.GrowthDatabase
import com.arurbangarden.real.data.database.GrowthRepository
import com.arurbangarden.real.databinding.ActivityGrowthRecordDetailBinding
import com.arurbangarden.real.util.SafetyMode
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider

class GrowthRecordDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGrowthRecordDetailBinding
    private lateinit var repository: GrowthRepository
    private var recordId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrowthRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        recordId = intent.getStringExtra("record_id")
        
        val database = GrowthDatabase.getDatabase(this)
        repository = GrowthRepository(database.growthDao())
        
        setupUI()
        loadRecord()
    }
    
    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnExport.setOnClickListener {
            exportRecord()
        }
    }
    
    private fun loadRecord() {
        recordId?.let { id ->
            lifecycleScope.launch {
                // Get record from database
                val allRecords = repository.getAllRecords()
                allRecords.collect { records ->
                    val record = records.find { it.id == id }
                    record?.let {
                        displayRecord(it)
                    }
                }
            }
        }
    }
    
    private fun displayRecord(record: GrowthRecord) {
        // Load photo
        val photoFile = File(record.photoPath)
        if (photoFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.imagePhoto.setImageBitmap(bitmap)
        }
        
        // Set date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.textDate.text = dateFormat.format(Date(record.timestamp))
        
        // Set emoji
        if (record.metadata.emoji != null) {
            binding.textEmoji.text = record.metadata.emoji
            binding.textEmoji.visibility = View.VISIBLE
        } else {
            binding.textEmoji.visibility = View.GONE
        }
        
        // Set note
        if (!record.metadata.notes.isNullOrEmpty()) {
            binding.textNote.text = record.metadata.notes
            binding.textNote.visibility = View.VISIBLE
        } else {
            binding.textNote.visibility = View.GONE
        }
        
        // Set stickers
        if (!record.metadata.stickers.isNullOrEmpty()) {
            binding.textStickers.text = record.metadata.stickers.joinToString(" ")
            binding.textStickers.visibility = View.VISIBLE
        } else {
            binding.textStickers.visibility = View.GONE
        }
        
        // Set metadata
        val metadata = record.metadata
        val metadataText = buildString {
            metadata.height?.let { append("Height: ${it}cm\n") }
            metadata.diameter?.let { append("Diameter: ${it}cm\n") }
            metadata.soilMoisture?.let { append("Moisture: ${it}%\n") }
            metadata.weatherCondition?.let { append("Weather: $it\n") }
        }
        
        if (metadataText.isNotEmpty()) {
            binding.textMetadata.text = metadataText.trim()
            binding.textMetadata.visibility = View.VISIBLE
        } else {
            binding.textMetadata.visibility = View.GONE
        }
    }
    
    private fun exportRecord() {
        // Export functionality for teachers/parents
        // Requires parental consent and safety mode check
        if (!SafetyMode.hasParentalConsent(this)) {
            android.app.AlertDialog.Builder(this)
                .setTitle("Export")
                .setMessage(getString(R.string.growth_export_consent))
                .setPositiveButton("OK", null)
                .show()
            return
        }
        
        // Check if sharing is allowed
        if (!SafetyMode.isSocialSharingAllowed(this)) {
            android.app.AlertDialog.Builder(this)
                .setTitle("Export")
                .setMessage("Sharing is disabled in Safety Mode")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        
        recordId?.let { id ->
            lifecycleScope.launch {
                // Get record and export
                val allRecords = repository.getAllRecords()
                allRecords.collect { records ->
                    val record = records.find { it.id == id }
                    record?.let {
                        exportToShare(it)
                    }
                }
            }
        }
    }
    
    private fun exportToShare(record: GrowthRecord) {
        // Create share intent with photo and notes
        val photoFile = File(record.photoPath)
        if (photoFile.exists()) {
            val photoUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, photoUri)
                putExtra(Intent.EXTRA_TEXT, buildExportText(record))
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share Growth Record"))
        }
    }
    
    private fun buildExportText(record: GrowthRecord): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return buildString {
            append("Plant Growth Record\n")
            append("Date: ${dateFormat.format(Date(record.timestamp))}\n")
            record.metadata.notes?.let { append("Note: $it\n") }
            record.metadata.emoji?.let { append("Emoji: $it\n") }
            record.metadata.height?.let { append("Height: ${it}cm\n") }
        }
    }
}

