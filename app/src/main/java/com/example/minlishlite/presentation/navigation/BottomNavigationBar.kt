package com.example.minlishlite.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.dp
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.Surface

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide navbar on focused flows (onboarding, study, review today)
    if (currentRoute == Routes.ONBOARDING) return
    if (currentRoute?.startsWith("study/") == true) return
    if (currentRoute == Routes.STUDY_DUE_TODAY) return
    if (currentRoute == Routes.REVIEW_TODAY) return

    val activeRoute = when (currentRoute) {
        Routes.HOME -> Routes.HOME
        Routes.PROGRESS -> Routes.PROGRESS
        Routes.SETTINGS -> Routes.SETTINGS
        else -> Routes.DECKS
    }

    NavigationBar(
        modifier = modifier,
        containerColor = Surface,
        contentColor = OnSurface,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        NavigationDestination.bottomBarDestinations.forEach { destination ->
            val isSelected = activeRoute == destination.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(text = destination.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = PrimarySoft,
                    unselectedIconColor = OnSurfaceMuted,
                    unselectedTextColor = OnSurfaceMuted
                )
            )
        }
    }
}
