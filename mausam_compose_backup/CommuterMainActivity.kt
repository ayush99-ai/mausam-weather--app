package com.example.mausam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mausam.data.repository.MausamRepository
import com.example.mausam.ui.components.MausamBottomBar
import com.example.mausam.ui.components.MausamTopBar
import com.example.mausam.ui.navigation.Screen
import com.example.mausam.ui.screens.disasters.DisastersAlertsScreen
import com.example.mausam.ui.screens.gps.GpsNearbyScreen
import com.example.mausam.ui.screens.login.LoginScreen
import com.example.mausam.ui.screens.weather.WeatherScreen
import com.example.mausam.ui.theme.MausamTheme

class CommuterMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MausamTheme {
                MausamApp()
            }
        }
    }
}

@Composable
fun MausamApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    val profile by MausamRepository.profile.collectAsState()
    val currentWeather by MausamRepository.currentWeather.collectAsState()

    val isMainScreen = currentScreen != Screen.Login

    Scaffold(
        topBar = {
            if (isMainScreen) {
                MausamTopBar(
                    profile = profile,
                    currentWeather = currentWeather,
                    onScenarioSelected = { scenarioKey ->
                        MausamRepository.setWeatherPreset(scenarioKey)
                    }
                )
            }
        },
        bottomBar = {
            if (isMainScreen) {
                MausamBottomBar(
                    currentRoute = currentScreen.route,
                    onNavigate = { route ->
                        when (route) {
                            Screen.Weather.route -> currentScreen = Screen.Weather
                            Screen.GpsNearby.route -> currentScreen = Screen.GpsNearby
                            Screen.DisastersAlerts.route -> currentScreen = Screen.DisastersAlerts
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Login -> {
                    LoginScreen(
                        onLoginSuccess = {
                            currentScreen = Screen.Weather
                        }
                    )
                }
                Screen.Weather -> {
                    WeatherScreen(
                        profile = profile,
                        currentWeather = currentWeather,
                        onScenarioChange = { scenarioKey ->
                            MausamRepository.setWeatherPreset(scenarioKey)
                        }
                    )
                }
                Screen.GpsNearby -> {
                    GpsNearbyScreen()
                }
                Screen.DisastersAlerts -> {
                    DisastersAlertsScreen()
                }
            }
        }
    }
}
