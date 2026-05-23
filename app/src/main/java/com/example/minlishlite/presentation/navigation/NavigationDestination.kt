package com.example.minlishlite.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : NavigationDestination(Routes.HOME, "Home", Icons.Outlined.Home)
    object Decks : NavigationDestination(Routes.DECKS, "Decks", Icons.Outlined.Layers)
    object Progress : NavigationDestination(Routes.PROGRESS, "Progress", Icons.Outlined.BarChart)
    object Settings : NavigationDestination(Routes.SETTINGS, "Settings", Icons.Outlined.Settings)

    companion object {
        val bottomBarDestinations = listOf(Home, Decks, Progress, Settings)
    }
}
