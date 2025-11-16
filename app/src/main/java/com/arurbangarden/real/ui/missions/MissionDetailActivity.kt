package com.arurbangarden.real.ui.missions

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arurbangarden.real.ARUrbanGardenApplication
import com.arurbangarden.real.R
import com.arurbangarden.real.data.database.MissionDatabase
import com.arurbangarden.real.data.database.MissionRepository
import com.arurbangarden.real.data.model.MissionStatus
import com.arurbangarden.real.data.model.MissionType
import com.arurbangarden.real.databinding.ActivityMissionDetailBinding
import com.arurbangarden.real.ui.ar.ARActivity
import com.arurbangarden.real.ui.plant.PlantRecognitionActivity
import com.arurbangarden.real.ui.tracking.GrowthTrackingActivity
import kotlinx.coroutines.launch
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.TextView
import com.arurbangarden.real.data.model.Mission

class MissionDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMissionDetailBinding
    private lateinit var repository: MissionRepository
    private var missionId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        missionId = intent.getStringExtra("mission_id")
        
        val database = MissionDatabase.getDatabase(this)
        repository = MissionRepository(database.missionDao())
        
        setupUI()
        loadMission()
    }
    
    private fun setupUI() {
        binding.recyclerViewSteps.layoutManager = LinearLayoutManager(this)
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnStart.setOnClickListener {
            startMission()
        }
        
        binding.btnComplete.setOnClickListener {
            completeMission()
        }
    }
    
    private fun loadMission() {
        missionId?.let { id ->
            lifecycleScope.launch {
                val mission = repository.getMissionById(id)
                mission?.let {
                    displayMission(it)
                }
            }
        }
    }
    
    private fun displayMission(mission: Mission) {
        val app = applicationContext as ARUrbanGardenApplication
        val isTagalog = app.getLanguage() == "tagalog"
        
        binding.textTitle.text = if (isTagalog) mission.titleTagalog else mission.title
        binding.textDescription.text = if (isTagalog) mission.descriptionTagalog else mission.description
        
        // Set icon
        binding.iconMission.setImageResource(mission.iconRes)
        
        // Display steps
        val adapter = MissionStepAdapter(mission.steps, isTagalog)
        binding.recyclerViewSteps.adapter = adapter
        
        // Update buttons
        when (mission.status) {
            MissionStatus.PENDING -> {
                binding.btnStart.visibility = View.VISIBLE
                binding.btnComplete.visibility = View.GONE
            }
            MissionStatus.IN_PROGRESS -> {
                binding.btnStart.visibility = View.GONE
                binding.btnComplete.visibility = View.VISIBLE
            }
            MissionStatus.COMPLETED -> {
                binding.btnStart.visibility = View.GONE
                binding.btnComplete.visibility = View.GONE
                binding.textCompleted.visibility = View.VISIBLE
            }
            else -> {}
        }
        
        // Show badge if has reward
        if (mission.badgeReward != null) {
            binding.layoutBadge.visibility = View.VISIBLE
            binding.textBadge.text = "Badge: ${mission.badgeReward.name}"
        }
    }
    
    private fun startMission() {
        missionId?.let { id ->
            lifecycleScope.launch {
                val mission = repository.getMissionById(id)
                mission?.let {
                    val updated = it.copy(status = MissionStatus.IN_PROGRESS)
                    repository.updateMission(updated)
                    displayMission(updated)
                    
                    // Launch appropriate activity based on mission type
                    launchMissionActivity(it.type)
                }
            }
        }
    }
    
    private fun launchMissionActivity(type: MissionType) {
        val intent = when (type) {
            MissionType.IDENTIFY_PLANT, MissionType.PLACE_GARDEN, MissionType.MEASURE_PLANT -> {
                Intent(this, ARActivity::class.java).apply {
                    putExtra("mode", when (type) {
                        MissionType.IDENTIFY_PLANT -> "identify"
                        MissionType.PLACE_GARDEN -> "placement"
                        MissionType.MEASURE_PLANT -> "measurement"
                        else -> "identify"
                    })
                }
            }
            MissionType.PHOTO_TAKE, MissionType.GROWTH_RECORD -> {
                Intent(this, GrowthTrackingActivity::class.java)
            }
            else -> null
        }
        
        intent?.let { startActivity(it) }
    }
    
    private fun completeMission() {
        missionId?.let { id ->
            lifecycleScope.launch {
                val mission = repository.getMissionById(id)
                mission?.let {
                    val updated = it.copy(
                        status = MissionStatus.COMPLETED,
                        completedAt = System.currentTimeMillis()
                    )
                    repository.updateMission(updated)
                    
                    // Show success animation
                    showSuccessAnimation()
                    
                    // Play sound
                    playSuccessSound()
                    
                    finish()
                }
            }
        }
    }
    
    private fun showSuccessAnimation() {
        val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.layoutContent.startAnimation(animation)
    }
    
    private fun playSuccessSound() {
        // Play success sound
        // In production, use actual sound file
    }
}

// Step adapter
class MissionStepAdapter(
    private val steps: List<com.arurbangarden.real.data.model.MissionStep>,
    private val isTagalog: Boolean
) : androidx.recyclerview.widget.RecyclerView.Adapter<MissionStepAdapter.StepViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mission_step, parent, false)
        return StepViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position])
    }
    
    override fun getItemCount() = steps.size
    
    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textStep: TextView = itemView.findViewById(R.id.text_step)
        private val checkIcon: View = itemView.findViewById(R.id.icon_check)
        
        fun bind(step: com.arurbangarden.real.data.model.MissionStep) {
            textStep.text = if (isTagalog) step.instructionTagalog else step.instruction
            checkIcon.visibility = if (step.isCompleted) View.VISIBLE else View.GONE
        }
    }
}

