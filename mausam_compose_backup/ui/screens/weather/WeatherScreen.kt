package com.example.mausam.ui.screens.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausam.data.model.CommuteProfile
import com.example.mausam.data.model.CommuteWeather
import com.example.mausam.data.model.DepartureWindow
import com.example.mausam.data.model.HourlyForecast
import com.example.mausam.data.repository.MausamRepository
import com.example.mausam.ui.components.CartoonLogoBadge
import com.example.mausam.ui.theme.*

@Composable
fun WeatherScreen(
    profile: CommuteProfile,
    currentWeather: CommuteWeather,
    onScenarioChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Commuter Greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning, ${profile.driverName} 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightOnBackground
                )
                Text(
                    text = "Commute: ${profile.originName.split(",")[0]} ➔ Cyber City",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
            CartoonLogoBadge(size = 44.dp)
        }

        // Hero Weather Card (Material 3 Light)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SkyBluePrimaryContainer.copy(alpha = 0.5f),
                                Color.White
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Badge row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SkyBluePrimary
                        ) {
                            Text(
                                text = currentWeather.badge,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Live Route Telemetry",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Temp and Condition
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${currentWeather.tempC}°",
                                    fontSize = 54.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LightOnBackground,
                                    lineHeight = 54.sp
                                )
                                Text(
                                    text = "C",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyBluePrimary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Text(
                                text = "Feels like ${currentWeather.feelsLikeC}°C • ${currentWeather.condition}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )
                        }

                        // Weather Icon Illustration Badge
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(SunnyAmberSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentWeather.id == "storm") "⛈️"
                                else if (currentWeather.id == "flood_alert") "🌊"
                                else if (currentWeather.id == "clearing") "🌦️"
                                else if (currentWeather.id == "fog") "🌫️"
                                else "☀️",
                                fontSize = 36.sp
                            )
                        }
                    }

                    // Commuter Advice Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (currentWeather.roadGripIndex < 60) AlertRedContainer.copy(alpha = 0.6f)
                                else SkyBluePrimaryContainer.copy(alpha = 0.7f)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = currentWeather.advice,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (currentWeather.roadGripIndex < 60) AlertRedOnContainer else SkyBlueOnPrimaryContainer
                        )
                    }
                }
            }
        }

        // Commuter Safety & Road Grip Meter
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Commuter Road Grip Index",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${currentWeather.roadGripIndex}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (currentWeather.roadGripIndex > 75) RoadGreen
                        else if (currentWeather.roadGripIndex > 50) WarningOrange
                        else AlertRed
                    )
                }

                // Progress indicator
                LinearProgressIndicator(
                    progress = { currentWeather.roadGripIndex / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (currentWeather.roadGripIndex > 75) RoadGreen
                    else if (currentWeather.roadGripIndex > 50) WarningOrange
                    else AlertRed,
                    trackColor = Color(0xFFE2E8F0)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Status: ${currentWeather.roadGripLabel}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = if (currentWeather.rainCountdownMin > 0) "Rain in ${currentWeather.rainCountdownMin} min" else "Active Rainfall",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentWeather.rainCountdownMin in 1..20) AlertRed else Color(0xFF0369A1)
                    )
                }
            }
        }

        // Recommended Commute Departure Window
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⏰", fontSize = 16.sp)
                    Text(
                        text = "Commuter Departure Planner",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                MausamRepository.departureWindows.forEach { window ->
                    DepartureWindowItem(window = window)
                }
            }
        }

        // Commuter Vitals Grid (Visibility, Wind, Rain Rate, AQI)
        Text(
            text = "Commute Vitals",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VitalTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Visibility,
                label = "Visibility",
                value = "${currentWeather.visibilityKm} km",
                subtitle = if (currentWeather.visibilityKm < 2.0) "Use fog lights" else "Clear road ahead",
                iconTint = SkyBluePrimary
            )
            VitalTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Air,
                label = "Wind Speed",
                value = "${currentWeather.windKmh} km/h",
                subtitle = "Dir: ${currentWeather.windDirection}",
                iconTint = SunnyAmberSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VitalTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WaterDrop,
                label = "Rainfall Rate",
                value = "${currentWeather.rainfallRateMmHr} mm/h",
                subtitle = "${currentWeather.rainProbability}% probability",
                iconTint = Color(0xFF0284C7)
            )
            VitalTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Shield,
                label = "Commuter AQI",
                value = "${currentWeather.aqi}",
                subtitle = currentWeather.aqiStatus,
                iconTint = if (currentWeather.aqi > 200) AlertRed else RoadGreen
            )
        }

        // Hourly Commuter Forecast
        Text(
            text = "Hourly Route Forecast",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(MausamRepository.hourlyForecast) { item ->
                HourlyForecastCard(item = item)
            }
        }

        // Scenario Switcher Quick Bar
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Test Weather Scenarios for Commuters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScenarioChip(
                        modifier = Modifier.weight(1f),
                        label = "Monsoon",
                        isSelected = currentWeather.id == "storm",
                        onClick = { onScenarioChange("storm") }
                    )
                    ScenarioChip(
                        modifier = Modifier.weight(1f),
                        label = "Flood",
                        isSelected = currentWeather.id == "flood_alert",
                        onClick = { onScenarioChange("flood_alert") }
                    )
                    ScenarioChip(
                        modifier = Modifier.weight(1f),
                        label = "Drizzle",
                        isSelected = currentWeather.id == "clearing",
                        onClick = { onScenarioChange("clearing") }
                    )
                    ScenarioChip(
                        modifier = Modifier.weight(1f),
                        label = "Fog",
                        isSelected = currentWeather.id == "fog",
                        onClick = { onScenarioChange("fog") }
                    )
                    ScenarioChip(
                        modifier = Modifier.weight(1f),
                        label = "Clear",
                        isSelected = currentWeather.id == "clear",
                        onClick = { onScenarioChange("clear") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DepartureWindowItem(window: DepartureWindow) {
    val isBest = window.isOptimal
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isBest) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
        border = if (isBest) androidx.compose.foundation.BorderStroke(1.5.dp, RoadGreen) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = window.time,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isBest) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoadGreen
                        ) {
                            Text(
                                text = "RECOMMENDED",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${window.durationMin} mins transit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isBest) Color(0xFF065F46) else Color(0xFF334155)
                )
            }

            Text(
                text = "${window.weather} • ${window.traffic} • Safety: ${window.safetyScore}/100",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            if (isBest) {
                Text(
                    text = window.note,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF047857)
                )
            }
        }
    }
}

@Composable
fun VitalTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun HourlyForecastCard(item: HourlyForecast) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.width(86.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.time.split(" ")[0],
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
            Text(
                text = item.conditionEmoji,
                fontSize = 20.sp
            )
            Text(
                text = "${item.tempC}°",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "${item.rainProb}% rain",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = SkyBluePrimary
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (item.isPeakRush) SunnyAmberSecondaryContainer else Color(0xFFF1F5F9)
            ) {
                Text(
                    text = if (item.isPeakRush) "RUSH" else item.roadStatus,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isPeakRush) SunnyAmberOnSecondaryContainer else Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ScenarioChip(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) SkyBluePrimary else Color(0xFFF1F5F9),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF475569)
            )
        }
    }
}
