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
import com.example.touchgrass.service.StepCounterService.Companion.totalSteps
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import com.example.touchgrass.ui.home.HomeViewModel
import com.example.touchgrass.ui.hydration.HydrationViewModel
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.stepcounter.StepsGraphViewModel
import com.example.touchgrass.ui.theme.TouchgrassTheme
import com.example.touchgrass.utils.Constants.DRANK_AMOUNT_PREFERENCES
import com.example.touchgrass.utils.Constants.HYDRATION_TARGET_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_DAY_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_TARGET_PREFERENCES
import com.example.touchgrass.utils.Constants.STEPS_WEEK_PREFERENCES
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.WeekFields


class MainActivity : ComponentActivity() {
    companion object {
        private var bluetoothAdapter: BluetoothAdapter? = null

        private val Context.dataStore by preferencesDataStore(name = STEPS_PREFERENCES)

        private lateinit var stepCounterViewModel: StepCounterViewModel
        private lateinit var homeViewModel: HomeViewModel
        private lateinit var heartRateMonitorViewModel: HeartRateMonitorViewModel
        private lateinit var hydrationViewModel: HydrationViewModel
        private lateinit var stepsGraphViewModel: StepsGraphViewModel
    }

    private var previousTotalSteps = 0f
    private var previousDayOfMonth = 0f
    private var currentDayOfMonth = 0f
    private var currentWeekNumber = 0f
    private var previousWeekNumber = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stepCounterViewModel = StepCounterViewModel()
        homeViewModel = HomeViewModel()
        hydrationViewModel = HydrationViewModel()
        heartRateMonitorViewModel = HeartRateMonitorViewModel(application)
        stepsGraphViewModel = StepsGraphViewModel(application)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) !=
                    PackageManager.PERMISSION_GRANTED)) {
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
                val currentSteps: Float

                currentDayOfMonth = dateNow.dayOfMonth.toFloat()
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
                }

                if (previousTotalSteps == 0f) {
                    previousTotalSteps = totalSteps
                }

                if (currentDayOfMonth != previousDayOfMonth || currentWeekNumber != previousWeekNumber) {

                    if (currentWeekNumber != previousWeekNumber){

                        stepsGraphViewModel.deleteEntries()
                        for (dayOfWeek in 1..7){
                            stepsGraphViewModel.insertEntry(StepsGraph(dayOfWeek.toFloat(), 0f))
                        }
                        previousWeekNumber = currentWeekNumber
                    }

                    previousTotalSteps = totalSteps
                    previousDayOfMonth = currentDayOfMonth
                    hydrationViewModel.onDrankAmountUpdate(0)

                    // For the perms popup
                    lifecycleScope.launch {
                        saveData(STEPS_PREFERENCES, previousTotalSteps)
                        saveData(STEPS_DAY_PREFERENCES, currentDayOfMonth)
                        saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)
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
            saveData(STEPS_DAY_PREFERENCES, currentDayOfMonth)
            saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)
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

            val savedDayOfWeek = loadData(STEPS_DAY_PREFERENCES)
            previousDayOfMonth = savedDayOfWeek ?: 0f

            val savedWeekNumber = loadData(STEPS_WEEK_PREFERENCES)
            previousWeekNumber = savedWeekNumber ?: 0f

            loadData(STEPS_TARGET_PREFERENCES)?.let {
                stepCounterViewModel.onTargetStepsIndexUpdate(it)
            }
            loadData(HYDRATION_TARGET_PREFERENCES)?.let {
                hydrationViewModel.onNumberGoalUpdate(it.toInt())
            }
            loadData(DRANK_AMOUNT_PREFERENCES)?.let {
                hydrationViewModel.onDrankAmountUpdate(it.toInt())
            }

            updateStepsAndTimer()
        }
    }
}
