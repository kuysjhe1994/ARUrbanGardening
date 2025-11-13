package com.arurbangarden.real.data.database

import android.content.Context
import androidx.room.*
import com.arurbangarden.real.data.model.GrowthRecord
import com.arurbangarden.real.data.model.GrowthMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room database for storing growth tracking records
 */
@Database(
    entities = [GrowthRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GrowthDatabase : RoomDatabase() {
    abstract fun growthDao(): GrowthDao
    
    companion object {
        @Volatile
        private var INSTANCE: GrowthDatabase? = null
        
        fun getDatabase(context: Context): GrowthDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GrowthDatabase::class.java,
                    "growth_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Entity(tableName = "growth_records")
data class GrowthRecordEntity(
    @PrimaryKey val id: String,
    val plantId: String,
    val anchorId: String?,
    val timestamp: Long,
    val photoPath: String,
    val height: Float?,
    val diameter: Float?,
    val soilMoisture: Int?,
    val soilPH: Float?,
    val temperature: Float?,
    val weatherCondition: String?,
    val weatherTemperature: Float?,
    val weatherHumidity: Int?,
    val notes: String?
) {
    fun toGrowthRecord(): GrowthRecord {
        return GrowthRecord(
            id = id,
            plantId = plantId,
            anchorId = anchorId,
            timestamp = timestamp,
            photoPath = photoPath,
            metadata = GrowthMetadata(
                height = height,
                diameter = diameter,
                soilMoisture = soilMoisture,
                soilPH = soilPH,
                temperature = temperature,
                weatherCondition = weatherCondition,
                weatherTemperature = weatherTemperature,
                weatherHumidity = weatherHumidity,
                notes = notes
            )
        )
    }
    
    companion object {
        fun fromGrowthRecord(record: GrowthRecord): GrowthRecordEntity {
            return GrowthRecordEntity(
                id = record.id,
                plantId = record.plantId,
                anchorId = record.anchorId,
                timestamp = record.timestamp,
                photoPath = record.photoPath,
                height = record.metadata.height,
                diameter = record.metadata.diameter,
                soilMoisture = record.metadata.soilMoisture,
                soilPH = record.metadata.soilPH,
                temperature = record.metadata.temperature,
                weatherCondition = record.metadata.weatherCondition,
                weatherTemperature = record.metadata.weatherTemperature,
                weatherHumidity = record.metadata.weatherHumidity,
                notes = record.metadata.notes
            )
        }
    }
}

@Dao
interface GrowthDao {
    @Query("SELECT * FROM growth_records WHERE plantId = :plantId ORDER BY timestamp ASC")
    fun getRecordsByPlant(plantId: String): Flow<List<GrowthRecordEntity>>
    
    @Query("SELECT * FROM growth_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<GrowthRecordEntity>>
    
    @Query("SELECT * FROM growth_records WHERE id = :id")
    suspend fun getRecordById(id: String): GrowthRecordEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GrowthRecordEntity)
    
    @Delete
    suspend fun deleteRecord(record: GrowthRecordEntity)
    
    @Query("DELETE FROM growth_records WHERE plantId = :plantId")
    suspend fun deleteRecordsByPlant(plantId: String)
    
    @Query("DELETE FROM growth_records")
    suspend fun deleteAllRecords()
}

/**
 * Repository for growth tracking data
 */
class GrowthRepository(private val dao: GrowthDao) {
    
    fun getRecordsByPlant(plantId: String): kotlinx.coroutines.flow.Flow<List<GrowthRecord>> {
        return kotlinx.coroutines.flow.map(dao.getRecordsByPlant(plantId)) { entities ->
            entities.map { it.toGrowthRecord() }
        }
    }
    
    fun getAllRecords(): kotlinx.coroutines.flow.Flow<List<GrowthRecord>> {
        return kotlinx.coroutines.flow.map(dao.getAllRecords()) { entities ->
            entities.map { it.toGrowthRecord() }
        }
    }
    
    suspend fun insertRecord(record: GrowthRecord) {
        dao.insertRecord(GrowthRecordEntity.fromGrowthRecord(record))
    }
    
    suspend fun deleteRecord(record: GrowthRecord) {
        dao.deleteRecord(GrowthRecordEntity.fromGrowthRecord(record))
    }
    
    suspend fun deleteRecordsByPlant(plantId: String) {
        dao.deleteRecordsByPlant(plantId)
    }
}

