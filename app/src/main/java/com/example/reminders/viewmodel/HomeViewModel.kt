package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.alarm.AlarmScheduler
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.ui.component.ToastData
import com.example.reminders.util.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val reminders: List<Reminder> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filterCategoryId: Int? = null,
    val toast: ToastData? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reminderRepo: ReminderRepository,
    private val categoryRepo: CategoryRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _filterCategoryId = MutableStateFlow<Int?>(null)
    private val _toast = MutableStateFlow<ToastData?>(null)
    private var undoReminder: Reminder? = null   // single-level undo: the reminder before mutation
    private var toastJob: Job? = null

    val uiState: StateFlow<HomeUiState> = combine(
        reminderRepo.getActiveReminders(),
        categoryRepo.getAll(),
        _filterCategoryId
    ) { reminders, categories, filter ->
        val sorted = reminders.sortedBy { DateTimeUtil.nextFireTimeMs(it) }
        HomeUiState(reminders = sorted, categories = categories, filterCategoryId = filter)
    }.combine(_toast) { state, toast -> state.copy(toast = toast) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun setFilter(categoryId: Int?) { _filterCategoryId.value = categoryId }

    fun markDone(reminder: Reminder) {
        viewModelScope.launch {
            undoReminder = reminder
            val updated = reminderRepo.markDone(reminder)
            alarmScheduler.cancel(reminder.id)
            if (updated.archived) {
                showToast(ToastData("הועבר לארכיון", "ביטול") { undo() })
            } else {
                val dayName = DateTimeUtil.hebrewDayOfWeekFull(reminder.day ?: 0)
                showToast(ToastData("נתראה ביום $dayName הבא", "ביטול") { undo() })
                alarmScheduler.schedule(updated)
            }
        }
    }

    fun togglePause(reminder: Reminder) {
        viewModelScope.launch {
            undoReminder = reminder
            val updated = reminderRepo.setPaused(reminder, !reminder.paused)
            if (updated.paused) {
                alarmScheduler.cancel(reminder.id)
                showToast(ToastData("\u201E${reminder.name}\u201D בהשהיה", "ביטול") { undo() })
            } else {
                alarmScheduler.schedule(updated)
                showToast(ToastData("\u201E${reminder.name}\u201D חזרה לפעול", "ביטול") { undo() })
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            undoReminder = reminder
            reminderRepo.delete(reminder)
            alarmScheduler.cancel(reminder.id)
            showToast(ToastData("תזכורת נמחקה", "ביטול") { undo() })
        }
    }

    private fun undo() {
        val snapshot = undoReminder ?: return
        viewModelScope.launch {
            reminderRepo.save(snapshot)
            if (!snapshot.paused && !snapshot.archived) alarmScheduler.schedule(snapshot)
            undoReminder = null
            _toast.value = null
        }
    }

    private fun showToast(toast: ToastData) {
        toastJob?.cancel()
        _toast.value = toast
        toastJob = viewModelScope.launch {
            delay(4000)
            _toast.value = null
        }
    }
}
