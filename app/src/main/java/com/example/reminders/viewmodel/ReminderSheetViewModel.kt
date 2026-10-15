package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.alarm.AlarmScheduler
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.data.repository.SettingsRepository
import com.example.reminders.util.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ReminderDraft(
    val id: Int = 0,
    val name: String = "",
    val type: ReminderType = ReminderType.ONCE,
    val date: String = DateTimeUtil.todayDateString(),
    val day: Int? = null,
    val time: String = "09:00",
    val categoryId: Int? = null,
    val sound: String? = null,
    val volume: Int? = null,
    val notes: String? = null,
    val soundOverrideEnabled: Boolean = false
)

fun ReminderDraft.isValid() = name.isNotBlank() && categoryId != null

@HiltViewModel
class ReminderSheetViewModel @Inject constructor(
    private val reminderRepo: ReminderRepository,
    private val categoryRepo: CategoryRepository,
    private val settingsRepo: SettingsRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _draft = MutableStateFlow(ReminderDraft())
    val draft: StateFlow<ReminderDraft> = _draft.asStateFlow()

    val categories = categoryRepo.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadReminder(id: Int?) {
        viewModelScope.launch {
            if (id == null || id == 0) {
                val defaultCatId = settingsRepo.defaultCategoryId.first()
                val validId = if (defaultCatId != null && categoryRepo.getById(defaultCatId) != null) defaultCatId else null
                _draft.value = ReminderDraft(categoryId = validId)
                return@launch
            }
            val r = reminderRepo.getById(id) ?: return@launch
            _draft.value = ReminderDraft(
                id = r.id, name = r.name, type = r.type,
                date = r.date ?: DateTimeUtil.todayDateString(),
                day = r.day, time = r.time,
                categoryId = r.categoryId,
                sound = r.sound, volume = r.volume, notes = r.notes,
                soundOverrideEnabled = r.sound != null || r.volume != null
            )
        }
    }

    fun updateName(v: String) { _draft.value = _draft.value.copy(name = v) }
    fun updateType(v: ReminderType) { _draft.value = _draft.value.copy(type = v) }
    fun updateDate(v: String) { _draft.value = _draft.value.copy(date = v) }
    fun updateDay(v: Int) { _draft.value = _draft.value.copy(day = v) }
    fun updateTime(v: String) { _draft.value = _draft.value.copy(time = v) }
    fun updateCategory(id: Int) { _draft.value = _draft.value.copy(categoryId = id) }
    fun updateSound(v: String) { _draft.value = _draft.value.copy(sound = v) }
    fun updateVolume(v: Int) { _draft.value = _draft.value.copy(volume = v) }
    fun updateNotes(v: String?) { _draft.value = _draft.value.copy(notes = v) }
    fun setSoundOverride(enabled: Boolean) {
        _draft.value = _draft.value.copy(
            soundOverrideEnabled = enabled,
            sound = if (!enabled) null else _draft.value.sound,
            volume = if (!enabled) null else _draft.value.volume
        )
    }

    fun save(onComplete: (Int) -> Unit) {
        val d = _draft.value
        if (!d.isValid()) return
        viewModelScope.launch {
            val reminder = Reminder(
                id = d.id, name = d.name.trim(), type = d.type,
                date = if (d.type == ReminderType.ONCE) d.date else null,
                day = if (d.type == ReminderType.WEEKLY) d.day else null,
                time = d.time,
                categoryId = d.categoryId!!,
                sound = if (d.soundOverrideEnabled) d.sound else null,
                volume = if (d.soundOverrideEnabled) d.volume else null,
                notes = d.notes?.takeIf { it.isNotBlank() }
            )
            val savedId = reminderRepo.save(reminder)
            alarmScheduler.schedule(reminder.copy(id = savedId))
            onComplete(savedId)
        }
    }

    fun delete(onComplete: () -> Unit) {
        val id = _draft.value.id
        if (id == 0) return
        viewModelScope.launch {
            reminderRepo.deleteById(id)
            alarmScheduler.cancel(id)
            onComplete()
        }
    }
}
