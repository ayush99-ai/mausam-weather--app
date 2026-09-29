package com.example.mausam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausam.data.model.CommuteProfile
import com.example.mausam.data.model.CommuteWeather
import com.example.mausam.data.repository.MausamRepository
import com.example.mausam.ui.theme.SkyBluePrimary
import com.example.mausam.ui.theme.SkyBluePrimaryContainer
import com.example.mausam.ui.theme.SunnyAmberSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MausamTopBar(
    profile: CommuteProfile,
    currentWeather: CommuteWeather,
    onScenarioSelected: (String) -> Unit
) {
    var showScenarioMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Cartoon Logo + Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CartoonLogoBadge(size = 42.dp)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mausam",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SkyBluePrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SunnyAmberSecondary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = profile.vehicleType.iconEmoji + " " + profile.vehicleType.label.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunnyAmberSecondary
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GPS",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sector 54 • Route to Cyber City",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Right: Weather Simulation Preset Pill
            Box {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SkyBluePrimaryContainer,
                    modifier = Modifier.clickable { showScenarioMenu = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentWeather.badge.split(" ")[0] + " Sim",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyBluePrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Scenario",
                            modifier = Modifier.size(16.dp),
                            tint = SkyBluePrimary
                        )
                    }
                }

                DropdownMenu(
                    expanded = showScenarioMenu,
                    onDismissRequest = { showScenarioMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("⛈️ Monsoon Storm (Severe Rain)") },
                        onClick = {
                            onScenarioSelected("storm")
                            showScenarioMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🌊 Flash Flood (Underpass Alert)") },
                        onClick = {
                            onScenarioSelected("flood_alert")
                            showScenarioMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🌦️ Passing Shower (Drizzle)") },
                        onClick = {
                            onScenarioSelected("clearing")
                            showScenarioMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🌫️ Morning Fog & Smog") },
                        onClick = {
                            onScenarioSelected("fog")
                            showScenarioMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("☀️ Dry Clear Commute") },
                        onClick = {
                            onScenarioSelected("clear")
                            showScenarioMenu = false
                        }
                    )
                }
            }
        }
    }
}
