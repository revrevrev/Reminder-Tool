package com.example.reminders.data.db

import androidx.room.*
import com.example.reminders.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE archived = 0 ORDER BY id ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE archived = 1 ORDER BY archivedAt DESC")
    fun getArchivedReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE archived = 0 AND paused = 0")
    suspend fun getActiveNonPausedOnce(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Int): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<Reminder>)
}
