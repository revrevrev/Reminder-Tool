package com.example.reminders.data.repository

import com.example.reminders.data.db.ReminderDao
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import com.example.reminders.util.DateTimeUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val dao: ReminderDao) {
    fun getActiveReminders(): Flow<List<Reminder>> = dao.getActiveReminders()
    fun getArchivedReminders(): Flow<List<Reminder>> = dao.getArchivedReminders()
    suspend fun getActiveNonPaused(): List<Reminder> = dao.getActiveNonPausedOnce()
    suspend fun getById(id: Int): Reminder? = dao.getById(id)

    suspend fun save(reminder: Reminder): Int {
        return if (reminder.id == 0) dao.insert(reminder).toInt()
        else { dao.update(reminder); reminder.id }
    }

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
    suspend fun deleteById(id: Int) = dao.deleteById(id)

    suspend fun markDone(reminder: Reminder, nowMs: Long = System.currentTimeMillis()): Reminder {
        return when (reminder.type) {
            ReminderType.ONCE -> {
                val updated = reminder.copy(archived = true, archivedAt = nowMs, snoozeUntil = null)
                dao.update(updated)
                updated
            }
            ReminderType.WEEKLY -> {
                val occurrence = DateTimeUtil.currentOccurrenceMs(reminder, nowMs)
                val updated = reminder.copy(skipUntil = occurrence, snoozeUntil = null)
                dao.update(updated)
                updated
            }
        }
    }

    suspend fun snooze(reminder: Reminder, minutes: Int): Reminder {
        val occurrence = DateTimeUtil.nextFireTimeMs(reminder)
        val snoozedUntil = occurrence + minutes * 60_000L
        val updated = reminder.copy(snoozeUntil = snoozedUntil)
        dao.update(updated)
        return updated
    }

    suspend fun setPaused(reminder: Reminder, paused: Boolean): Reminder {
        val updated = reminder.copy(paused = paused, snoozeUntil = null)
        dao.update(updated)
        return updated
    }

    suspend fun restore(reminder: Reminder): Reminder {
        val updated = reminder.copy(archived = false, archivedAt = null)
        dao.update(updated)
        return updated
    }

}
