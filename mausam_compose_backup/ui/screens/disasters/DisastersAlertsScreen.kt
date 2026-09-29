package com.example.mausam.ui.screens.disasters

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausam.data.model.DisasterAlert
import com.example.mausam.data.model.DisasterIncident
import com.example.mausam.data.model.HazardPoint50m
import com.example.mausam.data.model.TrafficRadar50m
import com.example.mausam.data.repository.MausamRepository
import com.example.mausam.ui.theme.*

@Composable
fun DisastersAlertsScreen() {
    val context = LocalContext.current
    var selectedRadiusMeters by remember { mutableStateOf(50) }

    val activeAlert = MausamRepository.activeDisasterAlert
    val radar50m = MausamRepository.trafficRadar50m
    val disasterHistory = MausamRepository.disasterLog72Hours

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Disaster Alerts & 50m Radar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightOnBackground
                    )
                    Text(
                        text = "Real-time safety radar & 72-hour disaster log",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AlertRedContainer
                ) {
                    Text(
                        text = "LIVE SHIELD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AlertRedOnContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Active Disaster Alert Banner (Warning / Emergency)
        item {
            ActiveAlertCard(alert = activeAlert, onDetourClick = {
                Toast.makeText(context, "Rerouting to Elevated Flyover Bypass (100% dry transit)", Toast.LENGTH_LONG).show()
            })
        }

        // 50-Meter Immediate Traffic & Hazard Radar Section
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Radius Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "50m Commute Radar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Text(
                                text = "Immediate forward zone traffic telemetry",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Radius Filter Selector
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(2.dp)
                        ) {
                            listOf(50, 500, 5000).forEach { radius ->
                                val isSelected = selectedRadiusMeters == radius
                                val label = if (radius < 1000) "${radius}m" else "${radius / 1000}km"
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) SkyBluePrimary else Color.Transparent,
                                    modifier = Modifier.clickable { selectedRadiusMeters = radius }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF64748B),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Concentric Circular 50m Radar Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Radar50mCanvas(hazards = radar50m.hazardsIn50m)

                        // Top Overlay: Forward Hazard Note
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.9f),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AlertRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Slow traffic & puddle detected at 38m ahead",
                                    fontSize = 10.sp,
                                    color = Color(0xFFF8FAFC),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Bottom Overlay: Vehicle Position
                        Text(
                            text = "▲ YOUR VEHICLE (0m)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(6.dp)
                        )
                    }

                    // Telemetry Grid for 50m Radius
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadarTelemetryPill(
                            modifier = Modifier.weight(1f),
                            title = "50m Zone Speed",
                            value = "${radar50m.liveSpeedKmh} km/h",
                            subText = "Normal: ${radar50m.normalSpeedKmh} km/h",
                            isWarning = true
                        )
                        RadarTelemetryPill(
                            modifier = Modifier.weight(1f),
                            title = "Forward Puddle",
                            value = "${radar50m.waterPoolingDepthMm} mm",
                            subText = "At 46m curb edge",
                            isWarning = true
                        )
                        RadarTelemetryPill(
                            modifier = Modifier.weight(1f),
                            title = "50m Grip",
                            value = "${radar50m.roadSurfaceGripPercent}%",
                            subText = radar50m.hydroplaningRisk,
                            isWarning = radar50m.roadSurfaceGripPercent < 60
                        )
                    }

                    // Immediate Commuter Guidance
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💡", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "50m Tactical Advice",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = radar50m.recommendedAction,
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    }

                    // Forward Hazard Micro-List
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Detected Obstacles within 50m:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        radar50m.hazardsIn50m.forEach { hazard ->
                            HazardRow(hazard = hazard)
                        }
                    }
                }
            }
        }

        // Section Title: Recent Disasters (Past 72h)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌪️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent Disasters (Past 72h)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                Text(
                    text = "5 Logged Events",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Disaster History List
        items(disasterHistory) { incident ->
            DisasterIncidentCard(incident = incident)
        }

        // Emergency Helplines & SOS Card
        item {
            EmergencyHelplineCard(
                onEmergencyCall = { number ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$number")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Dialing Emergency: $number", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ActiveAlertCard(
    alert: DisasterAlert,
    onDetourClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AlertRed.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AlertRed
                ) {
                    Text(
                        text = alert.severityBadge,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = alert.distanceLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlertRed
                )
            }

            Text(
                text = alert.headline,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = alert.locationName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )

            Text(
                text = alert.advice,
                fontSize = 12.sp,
                color = Color(0xFF334155)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RoadGreenContainer
                ) {
                    Text(
                        text = "💧 High-Power Pumps Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onDetourClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Avoid Underpass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Radar50mCanvas(hazards: List<HazardPoint50m>) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepRadiusFraction by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_radius"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height * 0.90f
        val maxRadius = size.height * 0.80f

        // Draw concentric range rings (10m, 20m, 30m, 40m, 50m)
        val ringSteps = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
        val ringLabels = listOf("10m", "20m", "30m", "40m", "50m")

        ringSteps.forEachIndexed { idx, frac ->
            val r = maxRadius * frac
            drawCircle(
                color = Color(0xFF334155),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5f)
            )
        }

        // Draw animated scanning pulse wave
        drawCircle(
            color = Color(0xFF06B6D4).copy(alpha = (1f - sweepRadiusFraction) * 0.4f),
            radius = maxRadius * sweepRadiusFraction,
            center = Offset(cx, cy),
            style = Stroke(width = 3f)
        )

        // Forward corridor guide lines (-30 deg, 0 deg, +30 deg)
        val lineLen = maxRadius
        drawLine(
            color = Color(0xFF334155),
            start = Offset(cx, cy),
            end = Offset(cx, cy - lineLen),
            strokeWidth = 1.5f
        )
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(cx, cy),
            end = Offset(cx - lineLen * 0.5f, cy - lineLen * 0.86f),
            strokeWidth = 1f
        )
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(cx, cy),
            end = Offset(cx + lineLen * 0.5f, cy - lineLen * 0.86f),
            strokeWidth = 1f
        )

        // Plot hazard points in 50m bubble
        hazards.forEach { hazard ->
            val distFrac = (hazard.distanceMeters / 50f).coerceIn(0.1f, 1.0f)
            val rad = Math.toRadians((hazard.directionAngleDeg - 90).toDouble())
            val px = cx + (distFrac * maxRadius * Math.cos(rad)).toFloat()
            val py = cy + (distFrac * maxRadius * Math.sin(rad)).toFloat()

            val dotColor = when (hazard.severity) {
                "HIGH" -> Color(0xFFEF4444)
                "WARN" -> Color(0xFFF59E0B)
                else -> Color(0xFF10B981)
            }

            drawCircle(
                color = dotColor.copy(alpha = 0.35f),
                radius = 12f,
                center = Offset(px, py)
            )
            drawCircle(
                color = dotColor,
                radius = 6f,
                center = Offset(px, py)
            )
        }

        // Commuter vehicle center anchor
        drawCircle(color = Color(0xFF0284C7), radius = 8f, center = Offset(cx, cy))
        drawCircle(color = Color.White, radius = 4f, center = Offset(cx, cy))
    }
}

@Composable
fun RadarTelemetryPill(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subText: String,
    isWarning: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isWarning) Color(0xFFFFFBEB) else Color(0xFFF1F5F9),
        border = if (isWarning) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)) else null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isWarning) Color(0xFFB45309) else Color(0xFF0F172A)
            )
            Text(text = subText, fontSize = 9.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun HazardRow(hazard: HazardPoint50m) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (hazard.severity) {
                            "HIGH" -> AlertRed
                            "WARN" -> WarningOrange
                            else -> RoadGreen
                        }
                    )
            )
            Text(
                text = "${hazard.distanceMeters}m ahead",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = hazard.label,
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
        }

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = when (hazard.severity) {
                "HIGH" -> AlertRedContainer
                "WARN" -> WarningOrangeContainer
                else -> RoadGreenContainer
            }
        ) {
            Text(
                text = hazard.severity,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = when (hazard.severity) {
                    "HIGH" -> AlertRedOnContainer
                    "WARN" -> WarningOrangeOnContainer
                    else -> Color(0xFF15803D)
                },
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun DisasterIncidentCard(incident: DisasterIncident) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (incident.severity == "CRITICAL") AlertRedContainer else WarningOrangeContainer
                ) {
                    Text(
                        text = incident.severity,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (incident.severity == "CRITICAL") AlertRedOnContainer else WarningOrangeOnContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = incident.timeAgo,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Text(
                text = incident.type,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "${incident.location} • ${incident.distance}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )

            Text(
                text = incident.summary,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Status: ${incident.clearedStatus}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EmergencyHelplineCard(
    onEmergencyCall: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚨", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Commuter Emergency & Disaster Helplines",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEmergencyCall("112") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Call 112 (Police/SOS)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onEmergencyCall("1077") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("1077 (NDRF Flood)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
