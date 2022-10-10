package com.example.touchgrass.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.osmdroid.util.GeoPoint

class MapViewModel {
    private val _geoPoint: MutableLiveData<GeoPoint> = MutableLiveData()
    val geoPoint: LiveData<GeoPoint> = _geoPoint


    fun geoPoint(lat: Double, lon: Double) {
        _geoPoint.value = GeoPoint(lat, lon)
    }


}