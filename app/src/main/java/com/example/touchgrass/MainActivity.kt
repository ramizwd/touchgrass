package com.example.touchgrass

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.touchgrass.data.database.StepsGraph
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import com.example.touchgrass.ui.hydration.HydrationViewModel
import com.example.touchgrass.ui.home.HomeViewModel
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.stepcounter.StepsGraphViewModel
import com.example.touchgrass.ui.theme.TouchgrassTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.WeekFields


class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        private lateinit var sensorManager: SensorManager
        private var stepCounter: Sensor? = null

        private const val TAG = "StepCounter"
        private const val STEPS_PREFERENCES = "steps"
        private const val STEPS_DAY_PREFERENCES = "step_counter_day"
        private const val STEPS_WEEK_PREFERENCES = "step_counter_week"
        private const val STEPS_TARGET_PREFERENCES = "step_target"
        private const val DRANK_AMOUNT = "drank_amount"
        private const val HYDRATION_TARGET = "number_goal"
        private const val STREAK_COUNTER = "streak_counter"
        private val Context.dataStore by preferencesDataStore(name = STEPS_PREFERENCES)

        private lateinit var stepCounterViewModel: StepCounterViewModel
        private lateinit var homeViewModel: HomeViewModel
        private lateinit var heartRateMonitorViewModel: HeartRateMonitorViewModel
        private lateinit var hydrationViewModel: HydrationViewModel
        private lateinit var stepsGraphViewModel: StepsGraphViewModel
    }

    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var previousDayOfMonth = 0f
    private var currentDayOfMonth = 0f
    private var currentWeekNumber = 0f
    private var previousWeekNumber = 0f
    private var streakCounter = 0f
    private var bluetoothAdapter: BluetoothAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepCounterViewModel = StepCounterViewModel()
        homeViewModel = HomeViewModel()
        hydrationViewModel = HydrationViewModel()
        heartRateMonitorViewModel = HeartRateMonitorViewModel(application)
        stepsGraphViewModel = StepsGraphViewModel(application)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) !=
                    PackageManager.PERMISSION_GRANTED)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ), 0
                )
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
                        homeViewModel,
                        heartRateMonitorViewModel,
                        bluetoothAdapter,
                        hydrationViewModel,
                        stepsGraphViewModel,
                    )
                }
            }
        }
    }

    /**
     * Handler for getting the time and changing variables based on the it.
     */
    private fun updateStepsAndTimer() {
        val timeHandler = Handler(mainLooper)

        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                val dateNow = LocalDateTime.now()
                val currentDayOfWeek = dateNow.dayOfWeek.value.toFloat()
                val weekFields = WeekFields.ISO
                val currentHour = dateNow.hour
                val currentMinute = dateNow.minute

                currentDayOfMonth = dateNow.dayOfMonth.toFloat()
                currentWeekNumber = dateNow.get(weekFields.weekOfWeekBasedYear()).toFloat()

                val totalMinutesOfDay = ((currentHour * 60) + currentMinute)
                val currentSteps = totalSteps - previousTotalSteps
                Log.d(TAG, "$totalSteps $previousTotalSteps")
                stepCounterViewModel.onStepsUpdate(currentSteps.toInt())
                homeViewModel.onHourUpdate(totalMinutesOfDay)
                stepsGraphViewModel.insertEntry(StepsGraph(currentDayOfWeek, currentSteps))

                if (previousTotalSteps == 0f) {
                    previousTotalSteps = totalSteps
                }

                homeViewModel.onStreaksUpdate(streakCounter)
                if (currentDayOfMonth != previousDayOfMonth || currentWeekNumber != previousWeekNumber) {
                    if (previousTotalSteps >= (stepCounterViewModel.targetStepsValue.value ?: 1000f)) {
                        streakCounter += 1f
                        Log.d("Streak", "$streakCounter")
                    } else {
                        streakCounter = 0f
                        Log.d("Streak", "$streakCounter")
                    }

                    if (currentWeekNumber != previousWeekNumber) {
                        stepsGraphViewModel.deleteEntries()
                        for (i in 1..7) {
                            stepsGraphViewModel.insertEntry(StepsGraph(i.toFloat(), 0f))
                        }
                        previousWeekNumber = currentWeekNumber
                    }

                    previousTotalSteps = totalSteps
                    previousDayOfMonth = currentDayOfMonth
                    hydrationViewModel.onDrankAmountUpdate(0)

                    lifecycleScope.launch {
                        saveData(STEPS_PREFERENCES, previousTotalSteps)
                        saveData(STEPS_DAY_PREFERENCES, currentDayOfMonth)
                        saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)
                        saveData(STREAK_COUNTER, streakCounter)
                        hydrationViewModel.numberGoal.value?.toFloat()?.let {
                            saveData(HYDRATION_TARGET, it)
                        }
                        hydrationViewModel.drankAmount.value?.toFloat()?.let {
                            saveData(DRANK_AMOUNT, it)
                        }
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
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    /**
     * DataStore functions for saving step counter sensor data and time
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
            saveData(STEPS_DAY_PREFERENCES, currentDayOfMonth)
            saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)
            stepCounterViewModel.targetStepsIndex.value?.let {
                saveData(STEPS_TARGET_PREFERENCES, it)
            }
            hydrationViewModel.numberGoal.value?.toFloat()?.let {
                saveData(HYDRATION_TARGET, it)
            }
            hydrationViewModel.drankAmount.value?.toFloat()?.let {
                saveData(DRANK_AMOUNT, it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounter?.also {
                sensorManager.registerListener(
                    this@MainActivity,
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
            previousDayOfMonth = savedDayOfWeek ?: 0f

            val savedWeekNumber = loadData(STEPS_WEEK_PREFERENCES)
            previousWeekNumber = savedWeekNumber ?: 0f

            val savedStreaks = loadData(STREAK_COUNTER)
            streakCounter = savedStreaks ?: 0f

            loadData(STEPS_TARGET_PREFERENCES)?.let {
                stepCounterViewModel.onTargetStepsIndexUpdate(it)
            }
            loadData(HYDRATION_TARGET)?.let {
                hydrationViewModel.onNumberGoalUpdate(it.toInt())
            }
            loadData(DRANK_AMOUNT)?.let {
                hydrationViewModel.onDrankAmountUpdate(it.toInt())
            }
        }
        updateStepsAndTimer()
    }
}
