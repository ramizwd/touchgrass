package com.example.touchgrass.ui.map

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.touchgrass.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


@Composable
fun MapScreen(mapViewModel: MapViewModel) {
    MapScreenBody(mapViewModel)
}

@Composable
fun MapScreenBody(mapViewModel: MapViewModel) {
    var lineMaker by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val locationFunction = LocationLiveData(context, mapViewModel)
    Column() {
        Button(onClick = {
            locationFunction.trackLocation()
            lineMaker = true
        }, modifier = Modifier.weight(0.05f)) {
            Text(text = "locate me")
        }
        Button(onClick = {
            locationFunction.stopTracking()

        }, modifier = Modifier.weight(0.05f)) {
            Text(text = "stop")
        }
        Column(modifier = Modifier.weight(0.25f)) {
            ShowMap(mapViewModel = mapViewModel, lineMaker = lineMaker)
        }
    }
}

@Composable
fun composeMap(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }

@Composable
fun ShowMap(
    mapViewModel: MapViewModel,
    onLoad: ((map: MapView) -> Unit)? = null,
    lineMaker: Boolean,
) {
    val map = composeMap()
    var mapInitialized by remember(map) { mutableStateOf(false) }
    val line = Polyline(map)

    val geoPoint: GeoPoint? by mapViewModel.geoPoint.observeAsState()


    val marker = Marker(map)
    if (!mapInitialized) {
        map.setTileSource(TileSourceFactory.OpenTopo)
        map.controller.setZoom(15.0)
        map.setMultiTouchControls(true)
        map.controller.setCenter(GeoPoint(60.17, 24.95))
        mapInitialized = true
    }
    AndroidView({ map }) { mapView ->
        onLoad?.invoke(mapView)
        geoPoint ?: return@AndroidView
        map.controller.setCenter(geoPoint)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        if (lineMaker) {
            line.addPoint(geoPoint)
            map.overlays.add(line)
        }
        map.overlays.add(marker)
        map.invalidate()
    }

}

/*@Composable
fun composeMap(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }
    return mapView
}

@Composable
fun ShowMap(mapViewModel: MapViewModel) {
    val map = composeMap()
    val line = Polyline(map)

    val geo by mapViewModel.geoPoint.observeAsState()
    val marker = Marker(map)
    var mapInitialized by remember(map) { mutableStateOf(false) }
    if (!mapInitialized) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(16.0)
        map.controller.setCenter(GeoPoint(60.17, 24.95))
        map.setMultiTouchControls(true)
        mapInitialized = true
    }
    AndroidView({ map }) {
        geo ?: return@AndroidView
        it.controller.setCenter(geo)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.position = geo
        marker.closeInfoWindow()
        map.setMultiTouchControls(true)
        line.addPoint(geo)
        map.overlays.add(line)
        map.overlays.add(marker)
        map.invalidate()
    }*/
/*}*/
