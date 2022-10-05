package com.example.touchgrass

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.home.HomeViewModel
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.theme.TouchgrassTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.LocalDateTime


class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        private lateinit var sensorManager: SensorManager
        private var stepCounter: Sensor? = null

        private const val TAG = "StepCounter"
        private const val STEPS_PREFERENCES = "steps"
        private const val STEPS_DAY_PREFERENCES = "StepCounterTime"

        private val Context.dataStore by preferencesDataStore(name = STEPS_PREFERENCES)

        private lateinit var stepCounterViewModel: StepCounterViewModel
        private lateinit var homeViewModel: HomeViewModel
    }

    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var previousDayOfWeek = 0f
    private var currentDayOfWeek = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepCounterViewModel = StepCounterViewModel()
        homeViewModel = HomeViewModel()

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) !=
                    PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
            }
        }

        setContent {
            TouchgrassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(
                        stepCounterViewModel,
                        homeViewModel
                    )
                }
            }
        }
    }

    private fun updateStepsAndTimer() {
        val timeHandler = Handler(mainLooper)
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                currentDayOfWeek = LocalDateTime.now().dayOfWeek.value.toFloat()
                val currentHour = LocalDateTime.now().hour
                val currentMinute = LocalDateTime.now().minute
                val totalMinutesOfDay = ((currentHour * 60) + currentMinute)
                val currentSteps = totalSteps - previousTotalSteps

                stepCounterViewModel.onStepsUpdate(currentSteps.toInt())
                homeViewModel.onHourUpdate(totalMinutesOfDay)

                if (currentDayOfWeek != previousDayOfWeek) {
                    previousTotalSteps = totalSteps
                    previousDayOfWeek = currentDayOfWeek

                    lifecycleScope.launch {
                        saveData(STEPS_DAY_PREFERENCES, currentDayOfWeek)
                    }
                }

                timeHandler.postDelayed(this, 1000)
            }
        }, 10)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        event.values.firstOrNull()?.let {
            Log.d(TAG, "Count: $it")
            totalSteps = it
        }

        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos = event.timestamp
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr = DateFormat.getDateInstance().format(actualSensorEventTimeInMillis)
        Log.d(TAG, "Sensor triggered at $displayDateStr")
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
        lifecycleScope.launch {
            saveData(STEPS_PREFERENCES, previousTotalSteps)
        }
//        sensorManager.unregisterListener(this@MainActivity)
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
            Log.d(TAG, "Sensor not found.")
        }

        lifecycleScope.launch {
            val savedSteps = loadData(STEPS_PREFERENCES)
            previousTotalSteps = savedSteps ?: 0f

            val savedDayOfWeek = loadData(STEPS_DAY_PREFERENCES)
            previousDayOfWeek = savedDayOfWeek ?: 0f
        }
        updateStepsAndTimer()
    }
}