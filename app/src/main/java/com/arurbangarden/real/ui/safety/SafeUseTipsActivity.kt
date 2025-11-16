package com.arurbangarden.real.ui.safety

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arurbangarden.real.ARUrbanGardenApplication
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivitySafeUseTipsBinding

class SafeUseTipsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySafeUseTipsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySafeUseTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        val app = applicationContext as ARUrbanGardenApplication
        val isTagalog = app.getLanguage() == "tagalog"
        
        binding.recyclerViewTips.layoutManager = LinearLayoutManager(this)
        
        val tips = if (isTagalog) {
            getTagalogTips()
        } else {
            getEnglishTips()
        }
        
        binding.recyclerViewTips.adapter = SafeUseTipsAdapter(tips)
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun getTagalogTips(): List<SafeUseTip> {
        return listOf(
            SafeUseTip(
                icon = "📱",
                title = "Gamitin nang Maingat",
                description = "Huwag tumakbo habang gumagamit ng phone. Mag-ingat sa paligid."
            ),
            SafeUseTip(
                icon = "🌱",
                title = "Magtanong sa Matanda",
                description = "Kung may hindi ka maintindihan, magtanong sa guro o magulang."
            ),
            SafeUseTip(
                icon = "📸",
                title = "Privacy",
                description = "Huwag kumuha ng larawan ng ibang tao nang walang permiso."
            ),
            SafeUseTip(
                icon = "🔒",
                title = "Ligtas na Data",
                description = "Lahat ng data ay naka-save sa iyong device lang. Ligtas!"
            ),
            SafeUseTip(
                icon = "👨‍👩‍👧",
                title = "Magulang/Guro",
                description = "Kailangan ng permiso ng magulang para sa ilang features."
            ),
            SafeUseTip(
                icon = "⏰",
                title = "Oras",
                description = "Huwag masyadong matagal sa phone. Magpahinga din!"
            )
        )
    }
    
    private fun getEnglishTips(): List<SafeUseTip> {
        return listOf(
            SafeUseTip(
                icon = "📱",
                title = "Use Carefully",
                description = "Don't run while using phone. Be aware of your surroundings."
            ),
            SafeUseTip(
                icon = "🌱",
                title = "Ask Adults",
                description = "If you don't understand something, ask your teacher or parent."
            ),
            SafeUseTip(
                icon = "📸",
                title = "Privacy",
                description = "Don't take photos of other people without permission."
            ),
            SafeUseTip(
                icon = "🔒",
                title = "Safe Data",
                description = "All data is saved on your device only. Safe!"
            ),
            SafeUseTip(
                icon = "👨‍👩‍👧",
                title = "Parent/Teacher",
                description = "Parent permission needed for some features."
            ),
            SafeUseTip(
                icon = "⏰",
                title = "Time",
                description = "Don't use phone too long. Take breaks!"
            )
        )
    }
}

data class SafeUseTip(
    val icon: String,
    val title: String,
    val description: String
)

class SafeUseTipsAdapter(private val tips: List<SafeUseTip>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SafeUseTipsAdapter.TipViewHolder>() {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TipViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_safe_tip, parent, false)
        return TipViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.bind(tips[position])
    }
    
    override fun getItemCount() = tips.size
    
    class TipViewHolder(itemView: android.view.View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val icon: android.widget.TextView = itemView.findViewById(R.id.text_icon)
        private val title: android.widget.TextView = itemView.findViewById(R.id.text_title)
        private val description: android.widget.TextView = itemView.findViewById(R.id.text_description)
        
        fun bind(tip: SafeUseTip) {
            icon.text = tip.icon
            title.text = tip.title
            description.text = tip.description
        }
    }
}

