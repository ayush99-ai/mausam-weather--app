package com.example.mausam.ui.screens.gps

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausam.data.model.ShopCategory
import com.example.mausam.data.model.ShopNecessity
import com.example.mausam.data.repository.MausamRepository
import com.example.mausam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsNearbyScreen() {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(ShopCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPoiId by remember { mutableStateOf<String?>(null) }

    val allPois = MausamRepository.nearbyShopsAndNecessities

    val filteredPois = remember(selectedCategory, searchQuery) {
        allPois.filter { poi ->
            val matchesCategory = selectedCategory == ShopCategory.ALL || poi.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    poi.name.contains(searchQuery, ignoreCase = true) ||
                    poi.categoryLabel.contains(searchQuery, ignoreCase = true) ||
                    poi.amenities.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Header with GPS status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Commuter GPS & Necessities",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightOnBackground
                    )
                    Text(
                        text = "Real-time roadside stops & emergency facilities",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoadGreenContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(RoadGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GPS LOCK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }
        }

        // Commuter Route GPS Canvas Map
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw background road grid lines
                        val gridColor = Color(0xFFF1F5F9)
                        for (i in 1..5) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, canvasHeight * (i / 6f)),
                                end = Offset(canvasWidth, canvasHeight * (i / 6f)),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = gridColor,
                                start = Offset(canvasWidth * (i / 6f), 0f),
                                end = Offset(canvasWidth * (i / 6f), canvasHeight),
                                strokeWidth = 2f
                            )
                        }

                        // Commuter Route Corridor Path
                        val path = Path().apply {
                            moveTo(canvasWidth * 0.15f, canvasHeight * 0.85f)
                            cubicTo(
                                canvasWidth * 0.35f, canvasHeight * 0.65f,
                                canvasWidth * 0.45f, canvasHeight * 0.45f,
                                canvasWidth * 0.85f, canvasHeight * 0.15f
                            )
                        }

                        // Corridor glow and line
                        drawPath(
                            path = path,
                            color = SkyBluePrimaryContainer,
                            style = Stroke(width = 16f)
                        )
                        drawPath(
                            path = path,
                            color = SkyBluePrimary,
                            style = Stroke(width = 6f)
                        )

                        // Origin Point (Home)
                        val origin = Offset(canvasWidth * 0.15f, canvasHeight * 0.85f)
                        drawCircle(color = Color(0xFF10B981), radius = 10f, center = origin)
                        drawCircle(color = Color.White, radius = 5f, center = origin)

                        // Destination Point (Work)
                        val destination = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f)
                        drawCircle(color = Color(0xFFEF4444), radius = 10f, center = destination)
                        drawCircle(color = Color.White, radius = 5f, center = destination)

                        // Commuter Live GPS Location (Pulsing blue point)
                        val liveUser = Offset(canvasWidth * 0.38f, canvasHeight * 0.60f)
                        drawCircle(color = SkyBluePrimary.copy(alpha = 0.25f), radius = 24f, center = liveUser)
                        drawCircle(color = SkyBluePrimary, radius = 9f, center = liveUser)
                        drawCircle(color = Color.White, radius = 4f, center = liveUser)

                        // POI markers on map
                        allPois.forEachIndexed { index, poi ->
                            val px = canvasWidth * (0.45f + poi.latOffset * 1.0f)
                            val py = canvasHeight * (0.50f + poi.lonOffset * 0.9f)
                            val isPoiSelected = poi.id == selectedPoiId
                            val pinColor = when (poi.category) {
                                ShopCategory.FUEL_EV -> Color(0xFF0284C7)
                                ShopCategory.PHARMACY -> Color(0xFF10B981)
                                ShopCategory.REPAIR -> Color(0xFFF59E0B)
                                ShopCategory.HOSPITAL -> Color(0xFFEF4444)
                                else -> Color(0xFF8B5CF6)
                            }
                            drawCircle(
                                color = if (isPoiSelected) Color(0xFF0F172A) else pinColor,
                                radius = if (isPoiSelected) 9f else 6f,
                                center = Offset(px, py)
                            )
                        }
                    }

                    // Map Overlay Legend / Info
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.92f),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📍", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cyber Corridor Live Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }
                    }

                    // Map Bottom labels
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🏠 Sector 54 Enclave", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                        Text("🏢 Cyber City Horizon", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search umbrellas, EV plugs, pharmacies, mechanic...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SkyBluePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = SkyBluePrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )
        }

        // Category Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ShopCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBluePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF475569)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) SkyBluePrimary else Color(0xFFE2E8F0),
                            selectedBorderColor = SkyBluePrimary,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // POI list section title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nearby Commuter Stops (${filteredPois.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Sorted by proximity",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // POI Cards
        items(filteredPois) { poi ->
            PoiCard(
                poi = poi,
                isSelected = poi.id == selectedPoiId,
                onSelect = { selectedPoiId = if (selectedPoiId == poi.id) null else poi.id },
                onCall = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${poi.phone.replace(" ", "")}")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Dialing: ${poi.phone}", Toast.LENGTH_SHORT).show()
                    }
                },
                onNavigate = {
                    Toast.makeText(context, "Navigating to ${poi.name} (${poi.distanceDisplay})", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PoiCard(
    poi: ShopNecessity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCall: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SkyBluePrimary) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category Badge + Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SkyBluePrimaryContainer
                ) {
                    Text(
                        text = poi.categoryLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlueOnPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = RoadGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${poi.distanceDisplay} • ${poi.travelTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                }
            }

            // Name
            Text(
                text = poi.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            // Status & Address
            Text(
                text = poi.status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0284C7)
            )

            Text(
                text = poi.address,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            // Amenities Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                poi.amenities.take(3).forEach { amenity ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = amenity,
                            fontSize = 10.sp,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigate,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Directions", fontSize = 12.sp)
                }
            }
        }
    }
}
