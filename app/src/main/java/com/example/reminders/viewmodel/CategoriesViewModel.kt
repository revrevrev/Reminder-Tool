package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val reminders: List<Reminder> = emptyList()
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val reminderRepo: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = combine(
        categoryRepo.getAll(),
        reminderRepo.getActiveReminders()
    ) { cats, reminders ->
        CategoriesUiState(categories = cats, reminders = reminders)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriesUiState())

    fun reminderCountFor(categoryId: Int, reminders: List<Reminder>) =
        reminders.count { it.categoryId == categoryId && !it.archived }
}
