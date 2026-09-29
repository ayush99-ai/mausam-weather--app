package com.example.mausam.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Weather : Screen("weather")
    object GpsNearby : Screen("gps_nearby")
    object DisastersAlerts : Screen("disasters_alerts")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Weather.route,
        title = "Weather",
        selectedIcon = Icons.Filled.Cloud,
        unselectedIcon = Icons.Outlined.Cloud
    ),
    BottomNavItem(
        route = Screen.GpsNearby.route,
        title = "GPS & Shops",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    ),
    BottomNavItem(
        route = Screen.DisastersAlerts.route,
        title = "Disasters & 50m",
        selectedIcon = Icons.Filled.Warning,
        unselectedIcon = Icons.Outlined.Warning,
        badgeCount = 1
    )
)
