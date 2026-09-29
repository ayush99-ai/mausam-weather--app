package com.example.farmers.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    public interface LocationResultListener {
        void onLocationFound(double lat, double lon, String locationName);
        void onError(String message);
    }

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    @SuppressWarnings("MissingPermission")
    public void getCurrentLocation(LocationResultListener listener) {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    resolveAddress(location, listener);
                } else {
                    requestFreshLocation(listener);
                }
            }).addOnFailureListener(e -> requestFreshLocation(listener));
        } catch (SecurityException e) {
            listener.onError("Location permission not granted.");
        }
    }

    @SuppressWarnings("MissingPermission")
    private void requestFreshLocation(LocationResultListener listener) {
        try {
            LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setMaxUpdates(1)
                    .build();

            fusedLocationClient.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location loc = locationResult.getLastLocation();
                    if (loc != null) {
                        resolveAddress(loc, listener);
                    } else {
                        listener.onError("Unable to determine current location.");
                    }
                    fusedLocationClient.removeLocationUpdates(this);
                }
            }, Looper.getMainLooper());
        } catch (SecurityException e) {
            listener.onError("Location permission denied: " + e.getMessage());
        }
    }

    private void resolveAddress(Location location, LocationResultListener listener) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String locName = "Lat: " + String.format(Locale.getDefault(), "%.2f", lat) +
                ", Lon: " + String.format(Locale.getDefault(), "%.2f", lon);

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                String locality = addr.getLocality();
                String adminArea = addr.getAdminArea();
                if (locality != null && !locality.isEmpty()) {
                    locName = locality + (adminArea != null ? ", " + adminArea : "");
                } else if (adminArea != null) {
                    locName = adminArea + ", " + addr.getCountryName();
                } else if (addr.getCountryName() != null) {
                    locName = addr.getCountryName();
                }
            }
        } catch (IOException ignored) {}

        listener.onLocationFound(lat, lon, locName);
    }
}
