package com.example.reminders.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.reminders.ui.screen.*
import com.example.reminders.ui.theme.*

private sealed class NavTab(val route: String, val label: String, val icon: ImageVector) {
    object Reminders : NavTab("home", "תזכורות", Icons.Outlined.NotificationsNone)
    object Categories : NavTab("categories", "קטגוריות", Icons.Outlined.GridView)
    object Settings : NavTab("settings", "הגדרות", Icons.Outlined.Tune)
}

private val TABS = listOf(NavTab.Reminders, NavTab.Categories, NavTab.Settings)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route ?: "home"
    val activeTab = when {
        currentRoute == "home" -> NavTab.Reminders.route
        currentRoute == "categories" -> NavTab.Categories.route
        else -> NavTab.Settings.route   // settings + archive both highlight Settings tab
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 10.dp)
                    .padding(top = 12.dp, bottom = 26.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TABS.forEach { tab ->
                        NavTabItem(
                            tab = tab,
                            selected = activeTab == tab.route,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("categories") { CategoriesScreen() }
            composable("settings") {
                SettingsScreen(onNavigateToArchive = { navController.navigate("archive") })
            }
            composable("archive") {
                ArchiveScreen(
                    onBack = { navController.popBackStack() },
                    onOpenEdit = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(tab: NavTab, selected: Boolean, onClick: () -> Unit) {
    val pillBg = if (selected) Accent200 else Bg
    val tint = if (selected) Accent800 else Neutral700
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(PillShape)
            .background(pillBg)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = if (selected) 16.dp else 12.dp, vertical = 5.dp)
    ) {
        Icon(tab.icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Text(tab.label, style = AppTypography.labelMedium, color = tint)
    }
}
