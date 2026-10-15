package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.data.model.Category
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val defaultSnooze: Int = 10,
    val archivedCount: Int = 0,
    val defaultCategoryId: Int? = null,
    val categories: List<Category> = emptyList()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val reminderRepo: ReminderRepository,
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepo.defaultSnoozeMinutes,
        reminderRepo.getArchivedReminders(),
        settingsRepo.defaultCategoryId,
        categoryRepo.getAll()
    ) { snooze, archived, defaultCatId, cats ->
        val validDefault = if (defaultCatId != null && cats.any { it.id == defaultCatId }) defaultCatId else null
        SettingsUiState(
            defaultSnooze = snooze,
            archivedCount = archived.size,
            defaultCategoryId = validDefault,
            categories = cats
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setDefaultSnooze(minutes: Int) {
        viewModelScope.launch { settingsRepo.setDefaultSnooze(minutes) }
    }

    fun setDefaultCategory(id: Int?) {
        viewModelScope.launch { settingsRepo.setDefaultCategoryId(id) }
    }
}
