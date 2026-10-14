package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val defaultSnooze: Int = 10,
    val archivedCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val reminderRepo: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepo.defaultSnoozeMinutes,
        reminderRepo.getArchivedReminders()
    ) { snooze, archived ->
        SettingsUiState(defaultSnooze = snooze, archivedCount = archived.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setDefaultSnooze(minutes: Int) {
        viewModelScope.launch { settingsRepo.setDefaultSnooze(minutes) }
    }
}
