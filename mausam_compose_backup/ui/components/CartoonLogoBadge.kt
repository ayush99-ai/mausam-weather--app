package com.example.mausam.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.farmers.R
import com.example.mausam.ui.theme.SkyBluePrimary
import com.example.mausam.ui.theme.SunnyAmberSecondary

@Composable
fun CartoonLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    elevation: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation, shape)
            .clip(shape)
            .background(Color.White)
            .border(2.dp, SunnyAmberSecondary.copy(alpha = 0.5f), shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_mausam_logo),
            contentDescription = "Mausam Cartoon Mascot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size)
        )
    }
}
