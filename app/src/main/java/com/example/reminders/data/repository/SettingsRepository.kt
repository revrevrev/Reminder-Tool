package com.example.reminders.data.repository

import com.example.reminders.data.prefs.PrefsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val prefs: PrefsDataStore) {
    val defaultSnoozeMinutes: Flow<Int> = prefs.defaultSnoozeMinutes
    suspend fun setDefaultSnooze(minutes: Int) = prefs.setDefaultSnooze(minutes)

    val defaultCategoryId: Flow<Int?> = prefs.defaultCategoryId
    suspend fun setDefaultCategoryId(id: Int?) = prefs.setDefaultCategoryId(id)
}
