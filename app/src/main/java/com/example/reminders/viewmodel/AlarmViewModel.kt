package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.alarm.AlarmScheduler
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlarmUiState(
    val reminder: Reminder? = null,
    val category: Category? = null,
    val snoozeMinutes: Int = 10
)

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val reminderRepo: ReminderRepository,
    private val categoryRepo: CategoryRepository,
    private val settingsRepo: SettingsRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _state.asStateFlow()

    fun loadReminder(id: Int) {
        viewModelScope.launch {
            val defaultSnooze = settingsRepo.defaultSnoozeMinutes.first()
            val reminder = reminderRepo.getById(id)
            val category = reminder?.let { categoryRepo.getById(it.categoryId) }
            _state.value = AlarmUiState(reminder = reminder, category = category, snoozeMinutes = defaultSnooze)
        }
    }

    fun setSnoozeMinutes(v: Int) {
        _state.value = _state.value.copy(snoozeMinutes = v.coerceIn(1, 120))
    }

    fun snooze(onDone: () -> Unit) {
        val reminder = _state.value.reminder ?: return
        viewModelScope.launch {
            val updated = reminderRepo.snooze(reminder, _state.value.snoozeMinutes)
            alarmScheduler.schedule(updated)
            onDone()
        }
    }

    fun markDone(onDone: () -> Unit) {
        val reminder = _state.value.reminder ?: return
        viewModelScope.launch {
            val updated = reminderRepo.markDone(reminder)
            alarmScheduler.cancel(reminder.id)
            if (reminder.type == ReminderType.WEEKLY) {
                alarmScheduler.schedule(updated)
            }
            onDone()
        }
    }

    fun effectiveSound(): String {
        val r = _state.value.reminder ?: return "chime"
        return r.sound ?: _state.value.category?.sound ?: "chime"
    }

    fun effectiveVolume(): Int {
        val r = _state.value.reminder ?: return 70
        return r.volume ?: _state.value.category?.volume ?: 70
    }

    fun effectiveContinuous(): Boolean {
        val r = _state.value.reminder ?: return false
        return r.continuous ?: _state.value.category?.continuous ?: false
    }
}
