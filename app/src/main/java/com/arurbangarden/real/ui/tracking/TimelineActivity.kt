package com.arurbangarden.real.ui.tracking

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arurbangarden.real.R
import com.arurbangarden.real.data.database.GrowthDatabase
import com.arurbangarden.real.data.database.GrowthRepository
import com.arurbangarden.real.data.model.GrowthRecord
import com.arurbangarden.real.databinding.ActivityTimelineBinding
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TimelineActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTimelineBinding
    private lateinit var repository: GrowthRepository
    private var plantId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        plantId = intent.getStringExtra("plant_id") ?: "default"
        
        val database = GrowthDatabase.getDatabase(this)
        repository = GrowthRepository(database.growthDao())
        
        setupUI()
        loadTimeline()
    }
    
    private fun setupUI() {
        binding.recyclerViewTimeline.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnAddPhoto.setOnClickListener {
            startActivity(Intent(this, AddGrowthPhotoActivity::class.java).apply {
                putExtra("plant_id", plantId)
            })
        }
    }
    
    private fun loadTimeline() {
        plantId?.let { id ->
            lifecycleScope.launch {
                repository.getRecordsByPlant(id).collect { records ->
                    if (records.isEmpty()) {
                        binding.textEmpty.visibility = View.VISIBLE
                        binding.recyclerViewTimeline.visibility = View.GONE
                    } else {
                        binding.textEmpty.visibility = View.GONE
                        binding.recyclerViewTimeline.visibility = View.VISIBLE
                        val adapter = TimelineAdapter(records) { record ->
                            openRecordDetail(record)
                        }
                        binding.recyclerViewTimeline.adapter = adapter
                    }
                }
            }
        }
    }
    
    private fun openRecordDetail(record: GrowthRecord) {
        // Open detail view
        val intent = Intent(this, GrowthRecordDetailActivity::class.java).apply {
            putExtra("record_id", record.id)
        }
        startActivity(intent)
    }
}

class TimelineAdapter(
    private val records: List<GrowthRecord>,
    private val onItemClick: (GrowthRecord) -> Unit
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline_entry, parent, false)
        return TimelineViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(records[position])
    }
    
    override fun getItemCount() = records.size
    
    inner class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image_thumbnail)
        private val date: TextView = itemView.findViewById(R.id.text_date)
        private val emoji: TextView = itemView.findViewById(R.id.text_emoji)
        private val note: TextView = itemView.findViewById(R.id.text_note)
        
        fun bind(record: GrowthRecord) {
            // Load thumbnail
            val photoFile = File(record.photoPath)
            if (photoFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                image.setImageBitmap(bitmap)
            }
            
            // Set date
            val dateStr = dateFormat.format(Date(record.timestamp))
            date.text = dateStr
            
            // Set emoji
            emoji.text = record.metadata.emoji ?: "🌱"
            emoji.visibility = if (record.metadata.emoji != null) View.VISIBLE else View.GONE
            
            // Set note (very short)
            val noteText = record.metadata.notes
            if (!noteText.isNullOrEmpty() && noteText.length <= 20) {
                note.text = noteText
                note.visibility = View.VISIBLE
            } else {
                note.visibility = View.GONE
            }
            
            itemView.setOnClickListener {
                onItemClick(record)
            }
        }
    }
}

