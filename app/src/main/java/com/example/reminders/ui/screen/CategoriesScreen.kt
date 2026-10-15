package com.example.reminders.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reminders.ui.component.CategoryCard
import com.example.reminders.ui.component.NewCategoryButton
import com.example.reminders.ui.sheet.CategorySheet
import com.example.reminders.ui.theme.*
import com.example.reminders.viewmodel.CategoriesViewModel
import com.example.reminders.viewmodel.CategorySheetViewModel

@Composable
fun CategoriesScreen(
    vm: CategoriesViewModel = hiltViewModel(),
    sheetVm: CategorySheetViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var editCategoryId by remember { mutableStateOf<Int?>(null) }

    if (showSheet) {
        val existingColors = state.categories.map { it.color }
        LaunchedEffect(editCategoryId) {
            sheetVm.loadCategory(editCategoryId, existingColors)
        }
        CategorySheet(
            vm = sheetVm,
            onDismiss = { showSheet = false; editCategoryId = null }
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 16.dp)) {
                Text("קטגוריות", style = AppTypography.headlineLarge, color = TextColor)
            }
        }

        items(state.categories, key = { it.id }) { cat ->
            val count = vm.reminderCountFor(cat.id, state.reminders)
            CategoryCard(
                category = cat,
                reminderCount = count,
                onClick = {
                    editCategoryId = cat.id
                    showSheet = true
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(2.dp))
        }

        item {
            NewCategoryButton(
                onClick = {
                    editCategoryId = null
                    showSheet = true
                },
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
