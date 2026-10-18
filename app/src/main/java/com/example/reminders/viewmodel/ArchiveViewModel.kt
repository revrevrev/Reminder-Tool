package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.ui.component.ToastData
import com.example.reminders.util.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArchiveUiState(
    val reminders: List<Reminder> = emptyList(),
    val toast: ToastData? = null
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val reminderRepo: ReminderRepository
) : ViewModel() {

    private val _toast = MutableStateFlow<ToastData?>(null)
    private var toastJob: Job? = null
    private var lastDeleted: Reminder? = null

    val uiState: StateFlow<ArchiveUiState> = reminderRepo.getArchivedReminders()
        .combine(_toast) { list, toast -> ArchiveUiState(reminders = list, toast = toast) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ArchiveUiState())

    fun restore(reminder: Reminder, onOpenEdit: (Int) -> Unit) {
        viewModelScope.launch {
            val restored = reminderRepo.restore(reminder)
            val needsBump = reminder.date != null &&
                    DateTimeUtil.parseDateTimeMs(reminder.date, reminder.time) < System.currentTimeMillis()
            val finalId = if (needsBump) {
                reminderRepo.save(restored.copy(date = DateTimeUtil.nextSameDayOfWeekDateString(reminder.date!!)))
            } else restored.id
            onOpenEdit(finalId)
        }
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            lastDeleted = reminder
            reminderRepo.delete(reminder)
            showToast(ToastData("תזכורת נמחקה", "ביטול") { undoDelete() })
        }
    }

    private fun undoDelete() {
        val r = lastDeleted ?: return
        viewModelScope.launch {
            reminderRepo.save(r)
            lastDeleted = null
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
