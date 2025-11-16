package com.arurbangarden.real.ui.tracking

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arurbangarden.real.R
import com.arurbangarden.real.data.database.GrowthDatabase
import com.arurbangarden.real.data.database.GrowthRepository
import com.arurbangarden.real.data.model.GrowthMetadata
import com.arurbangarden.real.data.model.GrowthRecord
import com.arurbangarden.real.databinding.ActivityAddGrowthPhotoBinding
import com.arurbangarden.real.ui.ar.ARActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddGrowthPhotoActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddGrowthPhotoBinding
    private lateinit var repository: GrowthRepository
    private var plantId: String? = null
    private var anchorId: String? = null
    private var selectedEmoji: String? = null
    private val REQUEST_CAMERA = 1
    private val REQUEST_AR_ANCHOR = 2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGrowthPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        plantId = intent.getStringExtra("plant_id") ?: "default"
        
        val database = GrowthDatabase.getDatabase(this)
        repository = GrowthRepository(database.growthDao())
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnSetAnchor.setOnClickListener {
            // Launch AR to set anchor
            startActivityForResult(
                Intent(this, ARActivity::class.java).apply {
                    putExtra("mode", "anchor_setup")
                },
                REQUEST_AR_ANCHOR
            )
        }
        
        binding.btnTakePhoto.setOnClickListener {
            if (anchorId == null) {
                // Warn but allow
                Toast.makeText(this, "Setting anchor helps keep same angle", Toast.LENGTH_SHORT).show()
            }
            takePhoto()
        }
        
        binding.btnSave.setOnClickListener {
            saveRecord()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // Emoji picker
        setupEmojiPicker()
        
        // Sticker picker (simple emoji-based)
        setupStickerPicker()
    }
    
    private fun setupEmojiPicker() {
        val emojis = listOf("🌱", "🌿", "🌳", "🌸", "🍅", "🥬", "🌶️", "💚", "📸", "✨")
        
        binding.recyclerViewEmojis.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        
        binding.recyclerViewEmojis.adapter = EmojiAdapter(emojis) { emoji ->
            selectedEmoji = emoji
            binding.textSelectedEmoji.text = emoji
            binding.textSelectedEmoji.visibility = View.VISIBLE
        }
    }
    
    private fun setupStickerPicker() {
        // Simple sticker picker using emojis
        val stickers = listOf("⭐", "🏆", "💪", "🎉", "👍", "❤️", "🌟", "🎈")
        
        binding.recyclerViewStickers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        
        binding.recyclerViewStickers.adapter = StickerAdapter(stickers) { sticker ->
            // Add sticker to note
            val currentNote = binding.editNote.text.toString()
            binding.editNote.setText("$currentNote $sticker")
        }
    }
    
    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        binding.imagePreview.setImageBitmap(it)
                        binding.imagePreview.visibility = View.VISIBLE
                        binding.btnSave.isEnabled = true
                    }
                }
            }
            REQUEST_AR_ANCHOR -> {
                if (resultCode == RESULT_OK) {
                    anchorId = data?.getStringExtra("anchor_id")
                    binding.textAnchorStatus.text = "Anchor set! ✅"
                    binding.textAnchorStatus.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun saveRecord() {
        val note = binding.editNote.text.toString().trim()
        
        // Validate note length (very short only)
        if (note.length > 30) {
            Toast.makeText(this, "Note too long! Keep it short.", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            // Save photo
            val photoPath = savePhotoToFile()
            
            if (photoPath == null) {
                Toast.makeText(this@AddGrowthPhotoActivity, "Error saving photo", Toast.LENGTH_SHORT).show()
                return@launch
            }
            
            // Create record
            val record = GrowthRecord(
                id = UUID.randomUUID().toString(),
                plantId = plantId ?: "default",
                anchorId = anchorId,
                timestamp = System.currentTimeMillis(),
                photoPath = photoPath,
                metadata = GrowthMetadata(
                    notes = if (note.isNotEmpty()) note else null,
                    emoji = selectedEmoji,
                    stickers = extractStickers(note)
                )
            )
            
            repository.insertRecord(record)
            
            Toast.makeText(this@AddGrowthPhotoActivity, "Saved! 🌱", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun savePhotoToFile(): String? {
        return try {
            val imageView = binding.imagePreview
            val drawable = imageView.drawable
            if (drawable is android.graphics.drawable.BitmapDrawable) {
                val bitmap = drawable.bitmap
                val filename = "growth_${System.currentTimeMillis()}.jpg"
                val file = File(getExternalFilesDir(null), "growth_photos")
                file.mkdirs()
                val photoFile = File(file, filename)
                
                FileOutputStream(photoFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                
                photoFile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun extractStickers(note: String): List<String>? {
        val stickerEmojis = listOf("⭐", "🏆", "💪", "🎉", "👍", "❤️", "🌟", "🎈")
        val found = stickerEmojis.filter { note.contains(it) }
        return if (found.isNotEmpty()) found else null
    }
}

// Simple emoji adapter
class EmojiAdapter(
    private val emojis: List<String>,
    private val onEmojiClick: (String) -> Unit
) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emoji, parent, false)
        return EmojiViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }
    
    override fun getItemCount() = emojis.size
    
    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textEmoji: TextView = itemView.findViewById(R.id.text_emoji)
        
        fun bind(emoji: String) {
            textEmoji.text = emoji
            itemView.setOnClickListener {
                onEmojiClick(emoji)
            }
        }
    }
}

// Simple sticker adapter
class StickerAdapter(
    private val stickers: List<String>,
    private val onStickerClick: (String) -> Unit
) : RecyclerView.Adapter<StickerAdapter.StickerViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sticker, parent, false)
        return StickerViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        holder.bind(stickers[position])
    }
    
    override fun getItemCount() = stickers.size
    
    inner class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textSticker: TextView = itemView.findViewById(R.id.text_sticker)
        
        fun bind(sticker: String) {
            textSticker.text = sticker
            itemView.setOnClickListener {
                onStickerClick(sticker)
            }
        }
    }
}

