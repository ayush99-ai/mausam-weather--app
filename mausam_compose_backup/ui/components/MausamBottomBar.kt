package com.example.mausam.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausam.ui.navigation.bottomNavItems
import com.example.mausam.ui.theme.AlertRed
import com.example.mausam.ui.theme.SkyBluePrimary

@Composable
fun MausamBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null) {
                                Badge(
                                    containerColor = AlertRed,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "!", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = if (isSelected) SkyBluePrimary else Color(0xFF64748B)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isSelected) SkyBluePrimary else Color(0xFF64748B)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE0F2FE)
                )
            )
        }
    }
}
