package com.example.mausam.data.repository

import com.example.mausam.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MausamRepository {

    private val _profile = MutableStateFlow(CommuteProfile())
    val profile: StateFlow<CommuteProfile> = _profile.asStateFlow()

    private val weatherPresets = mapOf(
        "storm" to CommuteWeather(
            id = "storm",
            name = "Monsoon Thunderstorm",
            badge = "⛈️ SEVERE MONSOON",
            tempC = 24,
            feelsLikeC = 27,
            condition = "Heavy Rain & Convective Cell",
            windKmh = 46,
            windDirection = "SW",
            visibilityKm = 2.1,
            humidityPercent = 94,
            rainProbability = 95,
            rainfallRateMmHr = 44,
            roadGripIndex = 48,
            roadGripLabel = "Slick / Hydroplaning Warning",
            rainCountdownMin = 12,
            advice = "🌧️ Monsoon cell active! Expect road puddling within 15 min. Elevated expressway recommended.",
            aqi = 45,
            aqiStatus = "Clean Rain-Washed Air"
        ),
        "clearing" to CommuteWeather(
            id = "clearing",
            name = "Passing Drizzle",
            badge = "🌦️ PASSING SHOWER",
            tempC = 27,
            feelsLikeC = 29,
            condition = "Light Intermittent Drizzle",
            windKmh = 24,
            windDirection = "W",
            visibilityKm = 6.5,
            humidityPercent = 82,
            rainProbability = 40,
            rainfallRateMmHr = 6,
            roadGripIndex = 78,
            roadGripLabel = "Damp Surface / Good Traction",
            rainCountdownMin = 45,
            advice = "🌤️ Rain easing off Eastward. Main corridors moving with moderate pace.",
            aqi = 85,
            aqiStatus = "Satisfactory Air"
        ),
        "flood_alert" to CommuteWeather(
            id = "flood_alert",
            name = "Flash Flood Warning",
            badge = "🌊 FLASH FLOOD RISK",
            tempC = 23,
            feelsLikeC = 26,
            condition = "Torrential Cloudburst",
            windKmh = 52,
            windDirection = "SSW",
            visibilityKm = 1.2,
            humidityPercent = 98,
            rainProbability = 100,
            rainfallRateMmHr = 68,
            roadGripIndex = 32,
            roadGripLabel = "Extreme Hydroplaning Hazard",
            rainCountdownMin = 0,
            advice = "⚠️ UNDERPASS HAZARD: Avoid low-lying underpasses. Take Flyover Bypass only.",
            aqi = 35,
            aqiStatus = "Pure Rain Wash"
        ),
        "fog" to CommuteWeather(
            id = "fog",
            name = "Dense Morning Smog / Fog",
            badge = "🌫️ LOW VISIBILITY",
            tempC = 18,
            feelsLikeC = 17,
            condition = "Dense Fog & Ground Smog",
            windKmh = 8,
            windDirection = "NW",
            visibilityKm = 0.6,
            humidityPercent = 90,
            rainProbability = 10,
            rainfallRateMmHr = 0,
            roadGripIndex = 72,
            roadGripLabel = "Moisture on Asphalt",
            rainCountdownMin = 180,
            advice = "🌫️ Visibility under 600m. Switch on low-beam fog lights and maintain 50m headway.",
            aqi = 280,
            aqiStatus = "Poor / Wear Commute Mask"
        ),
        "clear" to CommuteWeather(
            id = "clear",
            name = "Dry Clear Morning",
            badge = "☀️ OPTIMAL COMMUTE",
            tempC = 30,
            feelsLikeC = 32,
            condition = "Sunny & High Visibility",
            windKmh = 14,
            windDirection = "NW",
            visibilityKm = 10.0,
            humidityPercent = 55,
            rainProbability = 5,
            rainfallRateMmHr = 0,
            roadGripIndex = 98,
            roadGripLabel = "Optimal Dry Grip",
            rainCountdownMin = 0,
            advice = "☀️ Perfect road conditions. Expressway direct route fastest with no delays.",
            aqi = 112,
            aqiStatus = "Moderate Commute Air"
        )
    )

    private val _currentWeather = MutableStateFlow(weatherPresets["storm"]!!)
    val currentWeather: StateFlow<CommuteWeather> = _currentWeather.asStateFlow()

    fun setWeatherPreset(presetKey: String) {
        weatherPresets[presetKey]?.let {
            _currentWeather.value = it
        }
    }

    fun updateProfile(name: String, vehicleType: VehicleType) {
        _profile.value = _profile.value.copy(
            driverName = name,
            vehicleType = vehicleType
        )
    }

    val departureWindows = listOf(
        DepartureWindow(
            time = "08:00 AM",
            minutesFromNow = -10,
            durationMin = 25,
            traffic = "Light - Moderate",
            weather = "Darkening Clouds",
            safetyScore = 92,
            roadStatus = "Good Grip",
            note = "Past window: clouds gathered over northern arterial."
        ),
        DepartureWindow(
            time = "08:15 AM",
            minutesFromNow = 5,
            durationMin = 27,
            traffic = "Moderate Flow",
            weather = "Drizzle Starting",
            safetyScore = 88,
            roadStatus = "Road Damp",
            note = "Ideal if departing immediately."
        ),
        DepartureWindow(
            time = "08:25 AM",
            minutesFromNow = 15,
            durationMin = 28,
            traffic = "Optimal Flow",
            weather = "Rain in 35 mins",
            safetyScore = 96,
            roadStatus = "Clear & Swift",
            isOptimal = true,
            isRecommended = true,
            note = "⭐ BEST COMMUTE WINDOW: Arrives 10 mins before intense monsoon surge hits destination."
        ),
        DepartureWindow(
            time = "08:45 AM",
            minutesFromNow = 35,
            durationMin = 42,
            traffic = "Heavy Inflow",
            weather = "Heavy Downpour",
            safetyScore = 65,
            roadStatus = "Surface Water Accumulating",
            note = "Traffic bottleneck building; rain hits peak office rush."
        ),
        DepartureWindow(
            time = "09:05 AM",
            minutesFromNow = 55,
            durationMin = 54,
            traffic = "Severe Congestion",
            weather = "Thunderstorm Cell",
            safetyScore = 44,
            roadStatus = "High Hydroplaning Caution",
            note = "Underpass 4A expected to flood. Not recommended."
        )
    )

    val hourlyForecast = listOf(
        HourlyForecast("08:00 AM", 24, 75, "🌧️", "Damp", isPeakRush = true),
        HourlyForecast("09:00 AM", 23, 95, "⛈️", "Heavy Rain", isPeakRush = true),
        HourlyForecast("10:00 AM", 24, 85, "🌧️", "Water Pooling"),
        HourlyForecast("11:00 AM", 25, 60, "🌦️", "Passing Shower"),
        HourlyForecast("12:00 PM", 26, 40, "⛅", "Drying Out"),
        HourlyForecast("01:00 PM", 28, 25, "🌤️", "Optimal Grip"),
        HourlyForecast("02:00 PM", 29, 15, "☀️", "Dry Asphalt"),
        HourlyForecast("05:00 PM", 27, 45, "🌦️", "Damp Evening", isPeakRush = true),
        HourlyForecast("06:00 PM", 26, 70, "🌧️", "Evening Rain", isPeakRush = true)
    )

    val nearbyShopsAndNecessities = listOf(
        ShopNecessity(
            id = "poi-umbrella",
            category = ShopCategory.REST_CAFE,
            categoryLabel = "Kiosk & Umbrella Stall",
            name = "Metro Corner Umbrella & Rain Gear Kiosk",
            distanceMeters = 280,
            distanceDisplay = "280 m",
            travelTime = "1 min walk",
            status = "Open Now • Umbrellas & Raincoats in Stock",
            phone = "+91-98112-99011",
            address = "Gate 2, Rapid Metro Concourse",
            amenities = listOf("Windproof Umbrellas", "Emergency Ponchos", "Waterproof Mobile Pouches"),
            latOffset = 0.05f,
            lonOffset = -0.04f
        ),
        ShopNecessity(
            id = "poi-atm",
            category = ShopCategory.ATM,
            categoryLabel = "ATM & Cash Point",
            name = "SBI & HDFC 24/7 Multi-Bank Concourse ATM",
            distanceMeters = 450,
            distanceDisplay = "450 m",
            travelTime = "2 min",
            status = "Cash Available • Fast Queue",
            phone = "1800-425-3800",
            address = "Sector 54 Metro Station Lower Deck",
            amenities = listOf("Cash Dispenser", "Dry AC Shelter", "24/7 Security Guard"),
            latOffset = -0.08f,
            lonOffset = 0.06f
        ),
        ShopNecessity(
            id = "poi-pharmacy",
            category = ShopCategory.PHARMACY,
            categoryLabel = "24x7 Pharmacy",
            name = "Apollo 24/7 Drive-Thru Chemist",
            distanceMeters = 600,
            distanceDisplay = "600 m",
            travelTime = "2 min",
            status = "Open 24 Hours • Pharmacist On-Site",
            phone = "+91-124-4900024",
            address = "Galleria Market Boulevard, Block A",
            amenities = listOf("First-Aid Kits", "ORS & Cold Packs", "Drive-Thru Window"),
            latOffset = 0.12f,
            lonOffset = 0.15f
        ),
        ShopNecessity(
            id = "poi-fuel-ev",
            category = ShopCategory.FUEL_EV,
            categoryLabel = "Fuel & EV Fast Hub",
            name = "Shell Select & 150kW Ultra EV Superhub",
            distanceMeters = 850,
            distanceDisplay = "850 m",
            travelTime = "3 min",
            status = "Open • 4 Fast EV Chargers Free",
            phone = "+91-124-4289901",
            address = "Expressway Corridor Exit 7B",
            amenities = listOf("150kW Fast EV Plug", "Digital Tyre Gauge", "Clean Restrooms", "Costa Coffee"),
            latOffset = -0.15f,
            lonOffset = -0.18f
        ),
        ShopNecessity(
            id = "poi-shelter-cafe",
            category = ShopCategory.REST_CAFE,
            categoryLabel = "Commuter Haven & Cafe",
            name = "Highway Commuter Haven & Chai Cafe",
            distanceMeters = 1100,
            distanceDisplay = "1.1 km",
            travelTime = "3 min",
            status = "Open • Dry Covered Parking",
            phone = "+91-124-4112233",
            address = "Sector 43 Boulevard Plaza",
            amenities = listOf("Covered Basements", "Hot Ginger Chai", "Free High-Speed Wi-Fi", "Charging Ports"),
            latOffset = 0.22f,
            lonOffset = -0.12f
        ),
        ShopNecessity(
            id = "poi-police",
            category = ShopCategory.POLICE,
            categoryLabel = "Highway Police",
            name = "Cyber Expressway Traffic Police Post",
            distanceMeters = 1200,
            distanceDisplay = "1.2 km",
            travelTime = "4 min",
            status = "Patrol Active • Tow Unit On Standby",
            phone = "112 / +91-124-2300100",
            address = "Cyber City Entry Roundabout",
            amenities = listOf("Emergency Tow Truck", "Accident First Aid", "Live Route Advisory"),
            latOffset = 0.28f,
            lonOffset = 0.25f
        ),
        ShopNecessity(
            id = "poi-repair",
            category = ShopCategory.REPAIR,
            categoryLabel = "Tyre & Puncture Care",
            name = "Expressway 24/7 Tyre & Mobile Puncture Care",
            distanceMeters = 1400,
            distanceDisplay = "1.4 km",
            travelTime = "4 min",
            status = "Open • Mobile Rescue Van Ready",
            phone = "+91-98112-33445",
            address = "Service Road KM 4, Near Flyover Pillar 88",
            amenities = listOf("Nitrogen Air Station", "Tubeless Puncture Patch", "Jumpstart Battery Kit"),
            latOffset = -0.25f,
            lonOffset = 0.22f
        ),
        ShopNecessity(
            id = "poi-hospital",
            category = ShopCategory.HOSPITAL,
            categoryLabel = "Hospital & ER",
            name = "Fortis Emergency & Trauma Hub",
            distanceMeters = 1800,
            distanceDisplay = "1.8 km",
            travelTime = "5 min",
            status = "24/7 ER Operational",
            phone = "102 / +91-124-4921021",
            address = "Sector 44, opposite Metro Station",
            amenities = listOf("Emergency Trauma Ward", "Ambulance Fleet", "High-Flow Oxygen Backup"),
            latOffset = 0.35f,
            lonOffset = -0.30f
        )
    )

    val activeDisasterAlert = DisasterAlert(
        id = "alert-underpass-4a",
        severity = "CRITICAL",
        severityBadge = "⚠️ ACTIVE FLOOD ALERT",
        headline = "Heavy waterlogging detected on commuter route",
        subHeadline = "Rapid monsoon convective cell tracking Northeast directly along Cyber Corridor",
        distanceKm = 4.2,
        distanceLabel = "4.2 km ahead on route",
        locationName = "Underpass 4A & Cyber Corridor Arterial Junction",
        estimatedImpactTime = "Water accumulation expected in ~15 mins",
        currentRainIntensity = "44 mm/hr cloudburst band",
        advice = "Low-lying underpass is pooling water (32cm depth). Avoid surface road; divert to Safest Elevated Flyover Bypass.",
        livePumpsActive = true,
        alternateRouteAvailable = true
    )

    val trafficRadar50m = TrafficRadar50m(
        immediateRadiusMeters = 50,
        forwardSlowdownDistanceMeters = 38,
        forwardObstacleDescription = "Slow taxi crawling (18 km/h) & 22mm puddle at 38m",
        roadSurfaceGripPercent = 48,
        waterPoolingDepthMm = 22,
        liveSpeedKmh = 22,
        normalSpeedKmh = 48,
        delayMinutes = 6,
        hydroplaningRisk = "Moderate - High",
        recommendedAction = "Take Flyover ramp at 120m; maintain safe 35m vehicle spacing",
        hazardsIn50m = listOf(
            HazardPoint50m(12, 0f, "CLEAR", "Clear asphalt in front bumper zone", "SAFE"),
            HazardPoint50m(24, -15f, "SLICK_ROAD", "Oil & rainwater sheen on left tyre track", "WARN"),
            HazardPoint50m(38, 5f, "SLOW_VEHICLE", "Braking cab (18 km/h) with hazard flashers", "WARN"),
            HazardPoint50m(46, -22f, "PUDDLE", "22mm deep puddle along left kerb", "HIGH"),
            HazardPoint50m(49, 18f, "POTHOLE", "Submerged asphalt pothole on right edge", "HIGH")
        )
    )

    val disasterLog72Hours = listOf(
        DisasterIncident(
            id = "hist-01",
            type = "Flash Flood / Underpass Inundation",
            severity = "CRITICAL",
            timeAgo = "16 hours ago",
            exactTime = "Yesterday, 07:15 PM",
            location = "South City Underpass & Golf Course Ext.",
            distance = "3.8 km from your route",
            summary = "85mm cloudburst inundated low-lying arterial underpass with 55cm water. 4 cars rescued, high-capacity diesel pumps deployed.",
            clearedStatus = "Pumps active, water reduced to 8cm. Slow crawl."
        ),
        DisasterIncident(
            id = "hist-02",
            type = "Severe Cloud-to-Ground Lightning",
            severity = "HIGH",
            timeAgo = "22 hours ago",
            exactTime = "Yesterday, 01:40 PM",
            location = "Sector 56 Ridge Commuter Link",
            distance = "5.4 km away",
            summary = "18 intense lightning strikes recorded within 3km. Transformer burst caused highway signal blackout for 50 minutes.",
            clearedStatus = "Power restored, signals fully operational."
        ),
        DisasterIncident(
            id = "hist-03",
            type = "Slope Debris / Mudslide",
            severity = "HIGH",
            timeAgo = "46 hours ago",
            exactTime = "2 days ago, 01:20 PM",
            location = "Ridge Valley Mountain Link Road",
            distance = "8.1 km away",
            summary = "Heavy soil saturation triggered embankment soil slip across westbound dual carriage, blocking 2 lanes for commuters.",
            clearedStatus = "Bulldozers cleared debris; safety nets anchored."
        ),
        DisasterIncident(
            id = "hist-04",
            type = "Uprooted Peepal Tree Obstruction",
            severity = "MODERATE",
            timeAgo = "53 hours ago",
            exactTime = "2 days ago, 06:45 AM",
            location = "Old Arterial Highway KM 12",
            distance = "6.2 km away",
            summary = "Gale-force gusts (68 km/h) uprooted massive mature tree across middle lane during morning office hours.",
            clearedStatus = "Disaster Response Force sawed and cleared within 45 min."
        ),
        DisasterIncident(
            id = "hist-05",
            type = "Extreme Surface Heat & Asphalt Softening",
            severity = "MODERATE",
            timeAgo = "70 hours ago",
            exactTime = "3 days ago, 02:00 PM",
            location = "Metropolitan Ring Expressway",
            distance = "Along corridor",
            summary = "Road surface temperature peaked at 59.4°C causing asphalt softening and 6 tyre blowouts on high-speed lane.",
            clearedStatus = "Cooling rain shower normalized temperature."
        )
    )
}
