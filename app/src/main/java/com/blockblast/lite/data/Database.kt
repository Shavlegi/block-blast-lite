package com.blockblast.lite.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val mode: String,
    val date: Long
)

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val number: Int,
    val completed: Boolean,
    val bestScore: Int
)

@Entity(tableName = "daily")
data class DailyEntity(
    @PrimaryKey val date: String,
    val score: Int
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val sound: Boolean = true,
    val vibration: Boolean = true
)

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: ScoreEntity)

    @Query("SELECT MAX(score) FROM scores WHERE mode = :mode")
    suspend fun bestScore(mode: String): Int?

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 10")
    suspend fun topScores(): List<ScoreEntity>
}

@Dao
interface LevelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: LevelProgressEntity)

    @Query("SELECT * FROM level_progress WHERE number = :number")
    suspend fun get(number: Int): LevelProgressEntity?

    @Query("SELECT * FROM level_progress ORDER BY number ASC")
    suspend fun getAll(): List<LevelProgressEntity>
}

@Dao
interface DailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(daily: DailyEntity)

    @Query("SELECT * FROM daily WHERE date = :date")
    suspend fun get(date: String): DailyEntity?
}

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun get(): SettingsEntity?
}

@Database(
    entities = [
        ScoreEntity::class,
        LevelProgressEntity::class,
        DailyEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
    abstract fun levelDao(): LevelDao
    abstract fun dailyDao(): DailyDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "block-blast-lite.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
