package com.example.mausam.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausam.data.model.VehicleType
import com.example.mausam.data.repository.MausamRepository
import com.example.mausam.ui.components.CartoonLogoBadge
import com.example.mausam.ui.theme.SkyBluePrimary
import com.example.mausam.ui.theme.SkyBluePrimaryContainer
import com.example.mausam.ui.theme.SunnyAmberSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("Yashvardhan") }
    var selectedVehicle by remember { mutableStateOf(VehicleType.CAR) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Cartoon Mascot Logo Hero
            CartoonLogoBadge(
                size = 110.dp,
                shape = RoundedCornerShape(28.dp),
                elevation = 8.dp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Mausam",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SkyBluePrimary
                )
                Text(
                    text = "Smart Weather & Route Safety for Commuters",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }

            // Commuter Profile Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Commuter Sign-In",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Commuter Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SkyBluePrimary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "Select Primary Commute Mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )

                    // Vehicle Type selector grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VehicleType.values().forEach { vehicle ->
                            val isSelected = selectedVehicle == vehicle
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedVehicle = vehicle },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) SkyBluePrimaryContainer else Color(0xFFF1F5F9),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SkyBluePrimary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = vehicle.iconEmoji, fontSize = 22.sp)
                                    Text(
                                        text = vehicle.label.split(" ")[0],
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) SkyBluePrimary else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    // Commute Route preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Home: Sector 54, South Enclave", fontSize = 12.sp, color = Color(0xFF334155))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏁", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Work: Cyber City Horizon Tower 4", fontSize = 12.sp, color = Color(0xFF334155))
                            }
                        }
                    }
                }
            }

            // Commuter Safety Guarantee Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Real-time 50m radar & road flood telemetry active",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF15803D)
                )
            }

            // Start Commute Button
            Button(
                onClick = {
                    MausamRepository.updateProfile(name, selectedVehicle)
                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Start Daily Commute",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                }
            }

            Text(
                text = "Built for commuters navigating rain, floods & road traffic",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}
