package com.example.touchgrass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.theme.TouchgrassTheme
import java.text.DateFormat

class MainActivity : ComponentActivity(), SensorEventListener {
    companion object {
        private lateinit var sensorManager: SensorManager
        private var stepCounter: Sensor? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounter?.also {
                sensorManager.registerListener(this@MainActivity,
                    it,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }
        } else {
            Log.d("TYPE_STEP_COUNTER", "Sensor not found.")
        }

        setContent {
            TouchgrassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation()
                }
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        p0.values.firstOrNull()?.let {
            Log.d("TYPE_STEP_COUNTER", "Count: $it")
        }

        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos = p0.timestamp
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr = DateFormat.getDateInstance().format(actualSensorEventTimeInMillis)
        Log.d("TYPE_STEP_COUNTER", "Sensor triggered at $displayDateStr")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("TYPE_STEP_COUNTER", "onAccuracyChanged Sensor: $p0, Accuracy: $p1")
    }
}