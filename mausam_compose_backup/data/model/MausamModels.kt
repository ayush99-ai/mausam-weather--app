package com.example.mausam.data.model

enum class VehicleType(val label: String, val iconEmoji: String, val defaultSpeedKmh: Int) {
    CAR("Car / Taxi", "🚗", 42),
    BIKE("Bike / Scooter", "🛵", 35),
    METRO("Metro Transit", "🚇", 55),
    BUS("City Bus", "🚌", 25)
}

data class CommuteProfile(
    val driverName: String = "Yashvardhan",
    val vehicleType: VehicleType = VehicleType.CAR,
    val vehicleName: String = "Hyundai Ioniq 5 / AWD",
    val originName: String = "Green Glen Enclave, South Sector",
    val destinationName: String = "Cyber City Tech Horizon Tower 4",
    val lat: Double = 28.4595,
    val lon: Double = 77.0266,
    val avoidWaterlogging: Boolean = true
)

data class HourlyForecast(
    val time: String,
    val tempC: Int,
    val rainProb: Int,
    val conditionEmoji: String,
    val roadStatus: String,
    val isPeakRush: Boolean = false
)

data class DepartureWindow(
    val time: String,
    val minutesFromNow: Int,
    val durationMin: Int,
    val traffic: String,
    val weather: String,
    val safetyScore: Int,
    val roadStatus: String,
    val isOptimal: Boolean = false,
    val isRecommended: Boolean = false,
    val note: String
)

data class CommuteWeather(
    val id: String,
    val name: String,
    val badge: String,
    val tempC: Int,
    val feelsLikeC: Int,
    val condition: String,
    val windKmh: Int,
    val windDirection: String,
    val visibilityKm: Double,
    val humidityPercent: Int,
    val rainProbability: Int,
    val rainfallRateMmHr: Int,
    val roadGripIndex: Int, // e.g. 98%
    val roadGripLabel: String, // e.g. "Optimal Grip", "Slick / Hydroplaning"
    val rainCountdownMin: Int, // e.g. 25 min until rain
    val advice: String,
    val aqi: Int = 118,
    val aqiStatus: String = "Moderate Commuter Air"
)

enum class ShopCategory(val id: String, val label: String, val iconName: String) {
    ALL("all", "All Places", "apps"),
    FUEL_EV("fuel", "Fuel & EV Hub", "ev_station"),
    PHARMACY("pharmacy", "24x7 Pharmacy", "medication"),
    REPAIR("repair", "Tyre & Repair", "build"),
    REST_CAFE("shelter", "Chai, Cafe & Shelter", "local_cafe"),
    HOSPITAL("hospital", "Hospital & ER", "local_hospital"),
    POLICE("police", "Highway Police", "local_police"),
    ATM("atm", "Cash & ATM", "atm")
}

data class ShopNecessity(
    val id: String,
    val category: ShopCategory,
    val categoryLabel: String,
    val name: String,
    val distanceMeters: Int,
    val distanceDisplay: String,
    val travelTime: String,
    val status: String,
    val phone: String,
    val address: String,
    val amenities: List<String>,
    val latOffset: Float, // Relative map coords
    val lonOffset: Float
)

data class DisasterAlert(
    val id: String,
    val severity: String, // "CRITICAL", "HIGH", "MODERATE"
    val severityBadge: String,
    val headline: String,
    val subHeadline: String,
    val distanceKm: Double,
    val distanceLabel: String,
    val locationName: String,
    val estimatedImpactTime: String,
    val currentRainIntensity: String,
    val advice: String,
    val livePumpsActive: Boolean = true,
    val alternateRouteAvailable: Boolean = true
)

data class HazardPoint50m(
    val distanceMeters: Int, // e.g. 15, 30, 48
    val directionAngleDeg: Float, // 0 is straight ahead, -30 is left, 30 is right
    val hazardType: String, // "PUDDLE", "SLOW_VEHICLE", "POTHOLE", "SLICK_ROAD", "CLEAR"
    val label: String,
    val severity: String // "HIGH", "WARN", "SAFE"
)

data class TrafficRadar50m(
    val immediateRadiusMeters: Int = 50,
    val forwardSlowdownDistanceMeters: Int = 38,
    val forwardObstacleDescription: String = "Water pooling & slowed taxi at 38m",
    val roadSurfaceGripPercent: Int = 52,
    val waterPoolingDepthMm: Int = 18,
    val liveSpeedKmh: Int = 24,
    val normalSpeedKmh: Int = 45,
    val delayMinutes: Int = 7,
    val hydroplaningRisk: String = "Moderate Caution",
    val recommendedAction: String = "Shift to center lane; maintain 30m trailing distance",
    val hazardsIn50m: List<HazardPoint50m>
)

data class DisasterIncident(
    val id: String,
    val type: String,
    val severity: String,
    val timeAgo: String,
    val exactTime: String,
    val location: String,
    val distance: String,
    val summary: String,
    val clearedStatus: String
)
