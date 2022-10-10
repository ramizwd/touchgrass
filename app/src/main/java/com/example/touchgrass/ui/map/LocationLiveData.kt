package com.example.touchgrass.ui.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import org.osmdroid.config.Configuration

class LocationLiveData(private var context: Context, var mapViewModel: MapViewModel) {

    var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mapViewModel.geoPoint(location.latitude, location.longitude)
                Log.d(
                    "GEOLOCATION",
                    "location latitude:${location.latitude} and longitude:${location.longitude}"
                )
            }
        }
    }

    fun trackLocation() {
        val locationRequest = LocationRequest
            .create()
            .setInterval(6000)
            .setPriority(PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback, Looper.getMainLooper()
            )
        }
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}