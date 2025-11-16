package com.arurbangarden.real.ui.missions

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arurbangarden.real.R
import com.arurbangarden.real.data.database.MissionDatabase
import com.arurbangarden.real.data.database.MissionRepository
import com.arurbangarden.real.data.model.Mission
import com.arurbangarden.real.data.model.MissionStatus
import com.arurbangarden.real.data.mission.MissionGenerator
import com.arurbangarden.real.databinding.ActivityMissionsBinding
import kotlinx.coroutines.launch

class MissionsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMissionsBinding
    private lateinit var repository: MissionRepository
    private lateinit var adapter: MissionAdapter
    private var soundPlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize database
        val database = MissionDatabase.getDatabase(this)
        repository = MissionRepository(database.missionDao())
        
        setupUI()
        loadMissions()
        checkAndGenerateDailyMissions()
    }
    
    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MissionAdapter(
            onMissionClick = { mission -> openMissionDetail(mission) },
            onCompleteClick = { mission -> completeMission(mission) }
        )
        binding.recyclerView.adapter = adapter
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnRefresh.setOnClickListener {
            checkAndGenerateDailyMissions()
        }
        
        // Load progress
        loadProgress()
    }
    
    private fun loadMissions() {
        lifecycleScope.launch {
            repository.getAllActiveMissions().collect { missions ->
                if (missions.isEmpty()) {
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.textEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(missions)
                }
            }
        }
    }
    
    private fun checkAndGenerateDailyMissions() {
        // Check if missions are enabled
        val prefs = getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
        val missionsEnabled = prefs.getBoolean("missions_enabled", true)
        
        if (!missionsEnabled) {
            binding.textEmpty.text = "Missions are disabled by parent/teacher"
            binding.textEmpty.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            return
        }
        
        lifecycleScope.launch {
            repository.getAllActiveMissions().collect { missions ->
                // Generate daily missions if none exist
                if (missions.isEmpty()) {
                    generateDailyMissions()
                }
            }
        }
    }
    
    private fun generateDailyMissions() {
        lifecycleScope.launch {
            val dailyMissions = MissionGenerator.generateDailyMissions()
            dailyMissions.forEach { mission ->
                repository.insertMission(mission)
            }
            loadMissions()
        }
    }
    
    private fun openMissionDetail(mission: Mission) {
        // Open mission detail activity
        val intent = android.content.Intent(this, MissionDetailActivity::class.java).apply {
            putExtra("mission_id", mission.id)
        }
        startActivity(intent)
    }
    
    private fun completeMission(mission: Mission) {
        lifecycleScope.launch {
            val updatedMission = mission.copy(
                status = MissionStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            
            // Update steps to completed
            val completedSteps = updatedMission.steps.map { it.copy(isCompleted = true) }
            val finalMission = updatedMission.copy(steps = completedSteps)
            
            repository.updateMission(finalMission)
            
            // Play success sound
            playSuccessSound()
            
            // Show animation
            showCompletionAnimation()
            
            // Check for badge
            checkBadgeReward(finalMission)
            
            // Update progress
            loadProgress()
        }
    }
    
    private fun playSuccessSound() {
        try {
            // In production, use actual sound file
            // soundPlayer = MediaPlayer.create(this, R.raw.success_sound)
            // soundPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showCompletionAnimation() {
        // Show confetti or success animation
        val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.recyclerView.startAnimation(animation)
    }
    
    private fun checkBadgeReward(mission: Mission) {
        mission.badgeReward?.let { badge ->
            // Show badge earned dialog
            showBadgeDialog(badge)
        }
    }
    
    private fun showBadgeDialog(badge: com.arurbangarden.real.data.model.BadgeType) {
        // Show badge earned dialog
        android.app.AlertDialog.Builder(this)
            .setTitle("Badge Earned!")
            .setMessage("You earned: ${badge.name}")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun loadProgress() {
        lifecycleScope.launch {
            val progress = repository.getProgress()
            updateProgressBar(progress)
        }
    }
    
    private fun updateProgressBar(progress: com.arurbangarden.real.data.model.MissionProgress) {
        binding.progressBar.max = progress.totalMissions
        binding.progressBar.progress = progress.completedMissions
        
        binding.textProgress.text = getString(
            R.string.mission_progress,
            progress.completedMissions,
            progress.totalMissions
        )
        
        binding.textPoints.text = getString(
            R.string.mission_points,
            progress.pointsEarned
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.release()
    }
}

