package com.example.reminders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.data.model.CATEGORY_COLORS
import com.example.reminders.data.model.Category
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryDraft(
    val id: Int = 0,
    val name: String = "",
    val color: String = CATEGORY_COLORS[0],
    val sound: String = "chime",
    val volume: Int = 70,
    val continuous: Boolean = false
)

@HiltViewModel
class CategorySheetViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val reminderRepo: ReminderRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _draft = MutableStateFlow(CategoryDraft())
    val draft: StateFlow<CategoryDraft> = _draft.asStateFlow()

    val allCategories = categoryRepo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _deleteMode = MutableStateFlow(false)
    val deleteMode: StateFlow<Boolean> = _deleteMode.asStateFlow()

    private val _moveTarget = MutableStateFlow<Int?>(null)
    val moveTarget: StateFlow<Int?> = _moveTarget.asStateFlow()

    private val _reminderCount = MutableStateFlow(0)
    val reminderCount: StateFlow<Int> = _reminderCount.asStateFlow()

    private val _canDelete = MutableStateFlow(true)
    val canDelete: StateFlow<Boolean> = _canDelete.asStateFlow()

    fun loadCategory(id: Int?, existingColors: List<String>) {
        viewModelScope.launch {
            if (id == null || id == 0) {
                val firstUnused = CATEGORY_COLORS.firstOrNull { it !in existingColors } ?: CATEGORY_COLORS[0]
                _draft.value = CategoryDraft(color = firstUnused)
                _canDelete.value = false
                return@launch
            }
            val cat = categoryRepo.getById(id) ?: return@launch
            _draft.value = CategoryDraft(id = cat.id, name = cat.name, color = cat.color, sound = cat.sound, volume = cat.volume, continuous = cat.continuous)
            _canDelete.value = categoryRepo.count() > 1
            // Count reminders including archived
            // simplified: just check if any reminders use this category
        }
    }

    fun updateName(v: String) { _draft.value = _draft.value.copy(name = v) }
    fun updateColor(v: String) { _draft.value = _draft.value.copy(color = v) }
    fun updateSound(v: String) { _draft.value = _draft.value.copy(sound = v) }
    fun updateVolume(v: Int) { _draft.value = _draft.value.copy(volume = v) }
    fun updateContinuous(v: Boolean) { _draft.value = _draft.value.copy(continuous = v) }

    fun enterDeleteMode() { _deleteMode.value = true }
    fun exitDeleteMode() { _deleteMode.value = false; _moveTarget.value = null }
    fun setMoveTarget(id: Int) { _moveTarget.value = id }

    fun save(onComplete: () -> Unit) {
        val d = _draft.value
        if (d.name.isBlank()) return
        viewModelScope.launch {
            val cat = Category(id = d.id, name = d.name.trim(), color = d.color, sound = d.sound, volume = d.volume, continuous = d.continuous)
            categoryRepo.save(cat)
            onComplete()
        }
    }

    fun deleteAndMove(onComplete: () -> Unit) {
        val d = _draft.value
        val targetId = _moveTarget.value
        if (d.id == 0) return
        viewModelScope.launch {
            // Move reminders to targetId if set
            if (targetId != null) {
                val reminders = reminderRepo.getActiveNonPaused()
                reminders.filter { it.categoryId == d.id }.forEach {
                    reminderRepo.save(it.copy(categoryId = targetId))
                }
            }
            // If the deleted category was the default, update to moveTarget or null
            val currentDefault = settingsRepo.defaultCategoryId.first()
            if (currentDefault == d.id) {
                settingsRepo.setDefaultCategoryId(targetId)
            }
            val cat = categoryRepo.getById(d.id)
            cat?.let { categoryRepo.delete(it) }
            onComplete()
        }
    }

    fun isSoundPickerForCategory() = true
}
