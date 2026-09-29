package com.example.mausam.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MausamLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = SkyBlueOnPrimary,
    primaryContainer = SkyBluePrimaryContainer,
    onPrimaryContainer = SkyBlueOnPrimaryContainer,
    secondary = SunnyAmberSecondary,
    onSecondary = SkyBlueOnPrimary,
    secondaryContainer = SunnyAmberSecondaryContainer,
    onSecondaryContainer = SunnyAmberOnSecondaryContainer,
    tertiary = EmeraldTertiary,
    onTertiary = SkyBlueOnPrimary,
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = EmeraldOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = AlertRed,
    onError = SkyBlueOnPrimary,
    errorContainer = AlertRedContainer,
    onErrorContainer = AlertRedOnContainer
)

@Composable
fun MausamTheme(
    content: @Composable () -> Unit
) {
    // Light Theme is explicitly mandated for clean commuter visibility
    val colorScheme = MausamLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = LightBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
