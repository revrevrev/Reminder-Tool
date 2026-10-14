package com.example.reminders.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "reminders_prefs")

@Singleton
class PrefsDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val DEFAULT_SNOOZE = intPreferencesKey("default_snooze")
    private val UNDO_SNAPSHOT = stringPreferencesKey("undo_snapshot")

    val defaultSnoozeMinutes: Flow<Int> = context.dataStore.data.map { it[DEFAULT_SNOOZE] ?: 10 }

    suspend fun setDefaultSnooze(minutes: Int) {
        context.dataStore.edit { it[DEFAULT_SNOOZE] = minutes }
    }

    val undoSnapshot: Flow<String?> = context.dataStore.data.map { it[UNDO_SNAPSHOT] }

    suspend fun setUndoSnapshot(json: String?) {
        context.dataStore.edit {
            if (json == null) it.remove(UNDO_SNAPSHOT) else it[UNDO_SNAPSHOT] = json
        }
    }
}
