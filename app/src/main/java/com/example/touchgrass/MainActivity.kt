package com.example.touchgrass

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.example.touchgrass.data.database.StepsGraph
import com.example.touchgrass.service.StepCounterService.Companion.totalSteps
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import com.example.touchgrass.ui.home.HomeViewModel
import com.example.touchgrass.ui.hydration.HydrationViewModel
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.stepcounter.StepsGraphViewModel
import com.example.touchgrass.ui.theme.TouchgrassTheme
import com.example.touchgrass.utils.Constants.COUNTED_STEPS
import com.example.touchgrass.utils.Constants.DRANK_AMOUNT_PREFERENCES
import com.example.touchgrass.utils.Constants.HYDRATION_TARGET_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_DAY_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_TARGET_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_WEEK_PREFERENCES
import com.example.touchgrass.utils.Constants.STREAK_COUNTER
import com.example.touchgrass.utils.Constants.TARGET_STEPS
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.WeekFields

class MainActivity : ComponentActivity() {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = STEPS_PREFERENCES)

        private var bluetoothAdapter: BluetoothAdapter? = null

        private lateinit var stepCounterViewModel: StepCounterViewModel
        private lateinit var homeViewModel: HomeViewModel
        private lateinit var heartRateMonitorViewModel: HeartRateMonitorViewModel
        private lateinit var hydrationViewModel: HydrationViewModel
        private lateinit var stepsGraphViewModel: StepsGraphViewModel
    }

    private var previousTotalSteps = 0f
    private var previousDayOfYear = 0f
    private var currentDayOfYear = 0f
    private var currentWeekNumber = 0f
    private var previousWeekNumber = 0f
    private var streakCounter = 0f
    private var previousCountedSteps = 0f
    private var targetedSteps = 0f
    private var currentSteps = 0f

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
    private fun timerHandler() {
        val timeHandler = Handler(mainLooper)
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                val dateNow = LocalDateTime.now()
                val currentDayOfWeek = dateNow.dayOfWeek.value.toFloat()

                val weekFields = WeekFields.ISO
                val currentHour = dateNow.hour
                val currentMinute = dateNow.minute

                homeViewModel.onStreaksUpdate(streakCounter)
                currentDayOfYear = dateNow.dayOfYear.toFloat()
                currentWeekNumber = dateNow.get(weekFields.weekOfWeekBasedYear()).toFloat()

                val totalMinutesOfDay = ((currentHour * 60) + currentMinute)
                homeViewModel.onHourUpdate(totalMinutesOfDay)

                if (previousTotalSteps == 0f) {
                    previousTotalSteps = totalSteps
                }

                if (totalSteps != 0f) {
                    currentSteps = totalSteps - previousTotalSteps
                    stepCounterViewModel.onStepsUpdate(currentSteps.toInt())
                    stepsGraphViewModel.insertEntry(StepsGraph(currentDayOfWeek, currentSteps))
                    previousCountedSteps = currentSteps
                } else {
                    stepCounterViewModel.onStepsUpdate(previousCountedSteps.toInt())
                }

                if (currentDayOfYear != previousDayOfYear ||
                    currentWeekNumber != previousWeekNumber) {

                    if (currentWeekNumber != previousWeekNumber) {
                        stepsGraphViewModel.deleteEntries()
                        for (dayOfWeek in 1..7) {
                            stepsGraphViewModel
                                .insertEntry(StepsGraph(dayOfWeek.toFloat(), 0f))
                        }
                        previousWeekNumber = currentWeekNumber
                    }

                    // Logic for streak update
                    if (previousDayOfYear == currentDayOfYear - 1) {

                        if (targetedSteps <= previousCountedSteps) {
                            streakCounter++
                            homeViewModel.onStreaksUpdate(streakCounter)
                            previousCountedSteps = 0f

                        } else {
                            streakCounter = 0f
                            homeViewModel.onStreaksUpdate(streakCounter)
                        }
                    } else {
                        streakCounter = 0f
                        homeViewModel.onStreaksUpdate(streakCounter)
                    }

                    previousTotalSteps = totalSteps
                    previousDayOfYear = currentDayOfYear
                    hydrationViewModel.onDrankAmountUpdate(0)

                    // For the perms popup
                    lifecycleScope.launch {
                        saveData(STEPS_PREFERENCES, previousTotalSteps)
                        saveData(STEPS_DAY_PREFERENCES, currentDayOfYear)
                        saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)
                        saveData(STREAK_COUNTER, streakCounter)
                        saveData(COUNTED_STEPS, currentSteps)
                        stepCounterViewModel.targetStepsValue.value?.toFloat()?.let {
                            saveData(TARGET_STEPS, it)
                        }
                        hydrationViewModel.numberGoal.value?.toFloat()?.let {
                            saveData(HYDRATION_TARGET_PREFERENCES, it)
                        }
                        hydrationViewModel.drankAmount.value?.toFloat()?.let {
                            saveData(DRANK_AMOUNT_PREFERENCES, it)
                        }
                    }
                }
                timeHandler.postDelayed(this, 1000)
            }
        }, 10)
    }

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
            saveData(STEPS_DAY_PREFERENCES, currentDayOfYear)
            saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)
            saveData(COUNTED_STEPS, currentSteps)
            saveData(STREAK_COUNTER, streakCounter)
            stepCounterViewModel.targetStepsValue.value?.toFloat()?.let {
                saveData(TARGET_STEPS, it)
            }
            stepCounterViewModel.targetStepsIndex.value?.let {
                saveData(STEPS_TARGET_PREFERENCES, it)
            }
            hydrationViewModel.numberGoal.value?.toFloat()?.let {
                saveData(HYDRATION_TARGET_PREFERENCES, it)
            }
            hydrationViewModel.drankAmount.value?.toFloat()?.let {
                saveData(DRANK_AMOUNT_PREFERENCES, it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val savedSteps = loadData(STEPS_PREFERENCES)
            previousTotalSteps = savedSteps ?: 0f

            val savedDayOfYear = loadData(STEPS_DAY_PREFERENCES)
            previousDayOfYear = savedDayOfYear ?: 0f

            val savedWeekNumber = loadData(STEPS_WEEK_PREFERENCES)
            previousWeekNumber = savedWeekNumber ?: 0f

            val savedStreaks = loadData(STREAK_COUNTER)
            streakCounter = savedStreaks ?: 0f

            val savedPreviousCountedSteps = loadData(COUNTED_STEPS)
            previousCountedSteps = savedPreviousCountedSteps ?: 0f

            val savedTargetedSteps = loadData(TARGET_STEPS)
            targetedSteps = savedTargetedSteps ?: 0f

            loadData(STEPS_TARGET_PREFERENCES)?.let {
                stepCounterViewModel.onTargetStepsIndexUpdate(it)
            }
            loadData(HYDRATION_TARGET_PREFERENCES)?.let {
                hydrationViewModel.onNumberGoalUpdate(it.toInt())
            }
            loadData(DRANK_AMOUNT_PREFERENCES)?.let {
                hydrationViewModel.onDrankAmountUpdate(it.toInt())
            }

            timerHandler()
        }
    }
}
