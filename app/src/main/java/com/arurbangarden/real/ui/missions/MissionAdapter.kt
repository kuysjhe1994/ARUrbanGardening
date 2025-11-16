package com.arurbangarden.real.ui.missions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arurbangarden.real.ARUrbanGardenApplication
import com.arurbangarden.real.R
import com.arurbangarden.real.data.model.Mission
import com.arurbangarden.real.data.model.MissionStatus

class MissionAdapter(
    private val onMissionClick: (Mission) -> Unit,
    private val onCompleteClick: (Mission) -> Unit
) : ListAdapter<Mission, MissionAdapter.MissionViewHolder>(MissionDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mission, parent, false)
        return MissionViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class MissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon_mission)
        private val title: TextView = itemView.findViewById(R.id.text_title)
        private val description: TextView = itemView.findViewById(R.id.text_description)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_steps)
        private val btnComplete: View = itemView.findViewById(R.id.btn_complete)
        private val badgeIcon: ImageView = itemView.findViewById(R.id.icon_badge)
        
        fun bind(mission: Mission) {
            val app = itemView.context.applicationContext as ARUrbanGardenApplication
            val isTagalog = app.getLanguage() == "tagalog"
            
            // Set icon
            icon.setImageResource(mission.iconRes)
            
            // Set text based on language
            title.text = if (isTagalog) mission.titleTagalog else mission.title
            description.text = if (isTagalog) mission.descriptionTagalog else mission.description
            
            // Update progress
            val completedSteps = mission.steps.count { it.isCompleted }
            val totalSteps = mission.steps.size
            progressBar.max = totalSteps
            progressBar.progress = completedSteps
            
            // Show badge icon if has reward
            badgeIcon.visibility = if (mission.badgeReward != null) View.VISIBLE else View.GONE
            
            // Update complete button
            btnComplete.isEnabled = mission.status != MissionStatus.COMPLETED
            btnComplete.alpha = if (mission.status == MissionStatus.COMPLETED) 0.5f else 1.0f
            
            // Click listeners
            itemView.setOnClickListener {
                onMissionClick(mission)
            }
            
            btnComplete.setOnClickListener {
                onCompleteClick(mission)
            }
        }
    }
    
    class MissionDiffCallback : DiffUtil.ItemCallback<Mission>() {
        override fun areItemsTheSame(oldItem: Mission, newItem: Mission): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Mission, newItem: Mission): Boolean {
            return oldItem == newItem
        }
    }
}

