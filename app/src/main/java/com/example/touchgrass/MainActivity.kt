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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.theme.TouchgrassTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DateFormat


class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        private lateinit var sensorManager: SensorManager
        private var stepCounter: Sensor? = null
        private const val STEPS_PREFERENCES_NAME = "steps"
        private const val TYPE_STEP_COUNTER = "StepCounter"
        private lateinit var stepCounterViewModel: StepCounterViewModel
    }

    private val Context.dataStore by preferencesDataStore(name = STEPS_PREFERENCES_NAME)
    private var totalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepCounterViewModel = StepCounterViewModel()

        setContent {
            TouchgrassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(stepCounterViewModel)
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        event.values.firstOrNull()?.let {
            Log.d(TYPE_STEP_COUNTER, "Count: $it")
            totalSteps = it
            stepCounterViewModel.onStepsUpdate(totalSteps)
        }

        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos = event.timestamp
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr = DateFormat.getDateInstance().format(actualSensorEventTimeInMillis)
        Log.d(TYPE_STEP_COUNTER, "Sensor triggered at $displayDateStr")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    /**
     * DataStore functions for saving TYPE_STEP_COUNTER sensor data
     */
    private suspend fun saveData(key: String, value: Float) {
        val dataStoreKey = floatPreferencesKey(key)
        dataStore.edit { steps ->
            steps[dataStoreKey] = value
        }
    }

    private suspend fun loadData(key: String): Float? {
        val dataStoreKey = floatPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        lifecycleScope.launch {
            saveData(STEPS_PREFERENCES_NAME, totalSteps)
        }
    }

    override fun onResume() {
        super.onResume()

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
            Log.d(TYPE_STEP_COUNTER, "Sensor not found.")
        }

        lifecycleScope.launch {
            val savedSteps = loadData(STEPS_PREFERENCES_NAME)
            totalSteps = savedSteps ?: 0f
            Log.d(TYPE_STEP_COUNTER, "Saved steps: $savedSteps")
        }
    }
}