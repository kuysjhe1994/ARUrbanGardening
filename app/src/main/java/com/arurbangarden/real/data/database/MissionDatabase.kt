package com.arurbangarden.real.data.database

import android.content.Context
import androidx.room.*
import com.arurbangarden.real.data.model.Mission
import com.arurbangarden.real.data.model.MissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room database for storing missions
 */
@Database(
    entities = [MissionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MissionDatabase : RoomDatabase() {
    abstract fun missionDao(): MissionDao
    
    companion object {
        @Volatile
        private var INSTANCE: MissionDatabase? = null
        
        fun getDatabase(context: Context): MissionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MissionDatabase::class.java,
                    "mission_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleTagalog: String,
    val description: String,
    val descriptionTagalog: String,
    val type: String, // MissionType as string
    val stepsJson: String, // JSON string of steps
    val iconRes: Int,
    val badgeReward: String?, // BadgeType as string
    val points: Int,
    val isARIntegrated: Boolean,
    val createdAt: Long,
    val dueDate: Long?,
    val status: String, // MissionStatus as string
    val completedAt: Long?
) {
    fun toMission(): Mission {
        return Mission(
            id = id,
            title = title,
            titleTagalog = titleTagalog,
            description = description,
            descriptionTagalog = descriptionTagalog,
            type = MissionType.valueOf(type),
            steps = parseStepsJson(stepsJson),
            iconRes = iconRes,
            badgeReward = badgeReward?.let { BadgeType.valueOf(it) },
            points = points,
            isARIntegrated = isARIntegrated,
            createdAt = createdAt,
            dueDate = dueDate,
            status = MissionStatus.valueOf(status),
            completedAt = completedAt
        )
    }
    
    private fun parseStepsJson(json: String): List<com.arurbangarden.real.data.model.MissionStep> {
        // Simple JSON parsing - in production use proper JSON library
        // For now, return empty list - will be handled by repository
        return emptyList()
    }
    
    companion object {
        fun fromMission(mission: Mission): MissionEntity {
            return MissionEntity(
                id = mission.id,
                title = mission.title,
                titleTagalog = mission.titleTagalog,
                description = mission.description,
                descriptionTagalog = mission.descriptionTagalog,
                type = mission.type.name,
                stepsJson = mission.steps.joinToString("|") { "${it.id}:${it.instruction}:${it.instructionTagalog}:${it.isCompleted}:${it.order}" },
                iconRes = mission.iconRes,
                badgeReward = mission.badgeReward?.name,
                points = mission.points,
                isARIntegrated = mission.isARIntegrated,
                createdAt = mission.createdAt,
                dueDate = mission.dueDate,
                status = mission.status.name,
                completedAt = mission.completedAt
            )
        }
    }
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions WHERE status != 'EXPIRED' ORDER BY createdAt DESC")
    fun getAllActiveMissions(): Flow<List<MissionEntity>>
    
    @Query("SELECT * FROM missions WHERE status = :status ORDER BY createdAt DESC")
    fun getMissionsByStatus(status: String): Flow<List<MissionEntity>>
    
    @Query("SELECT * FROM missions WHERE id = :id")
    suspend fun getMissionById(id: String): MissionEntity?
    
    @Query("SELECT * FROM missions WHERE dueDate >= :today OR dueDate IS NULL ORDER BY dueDate ASC")
    fun getTodayMissions(today: Long = System.currentTimeMillis()): Flow<List<MissionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)
    
    @Update
    suspend fun updateMission(mission: MissionEntity)
    
    @Delete
    suspend fun deleteMission(mission: MissionEntity)
    
    @Query("DELETE FROM missions WHERE status = 'EXPIRED' AND completedAt IS NULL")
    suspend fun deleteExpiredMissions()
    
    @Query("SELECT COUNT(*) FROM missions WHERE status = 'COMPLETED'")
    suspend fun getCompletedCount(): Int
    
    @Query("SELECT COUNT(*) FROM missions WHERE status = 'IN_PROGRESS'")
    suspend fun getInProgressCount(): Int
}

/**
 * Repository for mission data
 */
class MissionRepository(private val dao: MissionDao) {
    
    fun getAllActiveMissions(): Flow<List<Mission>> {
        return dao.getAllActiveMissions().map { entities ->
            entities.map { it.toMission() }
        }
    }
    
    fun getMissionsByStatus(status: MissionStatus): Flow<List<Mission>> {
        return dao.getMissionsByStatus(status.name).map { entities ->
            entities.map { it.toMission() }
        }
    }
    
    fun getTodayMissions(): Flow<List<Mission>> {
        return dao.getTodayMissions().map { entities ->
            entities.map { it.toMission() }
        }
    }
    
    suspend fun getMissionById(id: String): Mission? {
        return dao.getMissionById(id)?.toMission()
    }
    
    suspend fun insertMission(mission: Mission) {
        dao.insertMission(MissionEntity.fromMission(mission))
    }
    
    suspend fun updateMission(mission: Mission) {
        dao.updateMission(MissionEntity.fromMission(mission))
    }
    
    suspend fun deleteMission(mission: Mission) {
        dao.deleteMission(MissionEntity.fromMission(mission))
    }
    
    suspend fun getProgress(): com.arurbangarden.real.data.model.MissionProgress {
        val completed = dao.getCompletedCount()
        val inProgress = dao.getInProgressCount()
        val allMissions = dao.getAllActiveMissions()
        // Calculate total and other metrics
        return com.arurbangarden.real.data.model.MissionProgress(
            totalMissions = completed + inProgress,
            completedMissions = completed,
            inProgressMissions = inProgress,
            pointsEarned = 0, // Calculate from completed missions
            badgesEarned = emptyList() // Calculate from completed missions
        )
    }
}

