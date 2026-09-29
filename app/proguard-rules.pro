# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK's default proguard-android-optimize.txt.

# Keep Gson serialized models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.example.farmers.data.model.** { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
