plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.farmers"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.farmers"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.mpandroidchart)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.glide)
    annotationProcessor(libs.glide)
    implementation(libs.lottie)
    implementation(libs.workmanager)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.location)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}