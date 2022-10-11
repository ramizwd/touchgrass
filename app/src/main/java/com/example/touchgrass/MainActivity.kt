package com.example.touchgrass

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
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
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.touchgrass.data.database.StepsGraph
import com.example.touchgrass.service.StepCounterService
import com.example.touchgrass.service.resetVariablesService
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import com.example.touchgrass.ui.home.HomeViewModel
import com.example.touchgrass.ui.hydration.HydrationViewModel
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.stepcounter.StepsGraphViewModel
import com.example.touchgrass.ui.theme.TouchgrassTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*


/**
 * TODO
 *  - put constants vals in util
 *  - get step to service notif
 *  - clean this mess
 */


class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        private lateinit var sensorManager: SensorManager
        private var stepCounter: Sensor? = null
        private var bluetoothAdapter: BluetoothAdapter? = null

        private const val TAG = "StepCounter"
        private const val STEPS_PREFERENCES = "steps"
        private const val STEPS_DAY_PREFERENCES = "step_counter_day"
        private const val STEPS_WEEK_PREFERENCES = "step_counter_week"
        private const val STEPS_TARGET_PREFERENCES = "step_target"
        private const val DRANK_AMOUNT = "drank_amount"
        private const val HYDRATION_TARGET = "number_goal"
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

    var isSensorOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        createNotificationChannel()
//        scheduleNotification()


        Log.d("ALARMHERE", "HERRROOO")

        stepCounterViewModel = StepCounterViewModel()
        homeViewModel = HomeViewModel()
        hydrationViewModel = HydrationViewModel()
        heartRateMonitorViewModel = HeartRateMonitorViewModel(application)
        stepsGraphViewModel = StepsGraphViewModel(application)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            requestPermissions(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ), 1
//            )
//        } else {
//            requestPermissions(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ), 1
//            )
//        }
//
//        if ((ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACTIVITY_RECOGNITION) !=
//                    PackageManager.PERMISSION_GRANTED)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(
//                        Manifest.permission.ACTIVITY_RECOGNITION
//                    ), 0
//                )
//            }
//        }

        setContent {
            TouchgrassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Button(onClick = {
                            Intent(application, StepCounterService::class.java).apply {
                                action = StepCounterService.ACTION_START
                                startService(this)
                            }
                            startStepCounterSenor()
                        }) {
                            Text(text = "Start")
                        }
                        Button(onClick = {
                            Intent(application, StepCounterService::class.java).apply {
                                action = StepCounterService.ACTION_STOP
                                startService(this)
                            }
                            stopStepsCounterSensor()
                            isSensorOn = false
                        }) {
                            Text(text = "Stop")
                        }
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
    }

    //--------------------------------------------
    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, com.example.touchgrass.Notification::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 0)


        val pi = PendingIntent.getService(
            this, 0,
            Intent(this, resetVariablesService::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time = getTime()
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pi
        )
        showAlert(calendar.timeInMillis)
    }
    private fun showAlert(time: Long)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }
    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    //--------------------------------------------

    private fun stopStepsCounterSensor() {
        if (isSensorOn) {
            sensorManager.unregisterListener(this)

        }
    }

    private fun startStepCounterSenor() {
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
            isSensorOn = true
        } else {
            Log.d(TAG, "Sensor not found.")
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

                homeViewModel.onHourUpdate(totalMinutesOfDay)
                if (previousTotalSteps == 0f) {
                    previousTotalSteps = totalSteps

                }

//                if (isSensorOn) {
//                Log.d("SENSORON", "$totalSteps - $previousTotalSteps $currentDayOfMonth != $previousDayOfMonth $currentWeekNumber != $previousWeekNumber")
                var currentSteps = 0f
                if (totalSteps != 0f) {
                    currentSteps = totalSteps - previousTotalSteps

                    stepCounterViewModel.onStepsUpdate(currentSteps.toInt())
                    stepsGraphViewModel.insertEntry(StepsGraph(currentDayOfWeek, currentSteps))
                    Log.d("SENSORON", "::::::-----$totalSteps - $previousTotalSteps ::: $currentDayOfWeek, $currentSteps")

                }

                if (currentDayOfMonth != previousDayOfMonth || currentWeekNumber != previousWeekNumber) {

                    if (currentWeekNumber != previousWeekNumber){
                        Log.d("SENSORON", "IF week ABOVE---- $totalSteps - $previousTotalSteps :: $currentWeekNumber $previousWeekNumber")

                        stepsGraphViewModel.deleteEntries()
                        for (dayOfWeek in 1..7){
                            stepsGraphViewModel.insertEntry(StepsGraph(dayOfWeek.toFloat(), 0f))
                        }
                        previousWeekNumber = currentWeekNumber
                        Log.d("SENSORON", "IF week---- $totalSteps - $previousTotalSteps :: $currentWeekNumber $previousWeekNumber")

                    }
//                    Log.d("SENSORON", "IF---- $totalSteps - $previousTotalSteps $currentDayOfMonth != $previousDayOfMonth $currentWeekNumber != $previousWeekNumber")
                    Log.d("SENSORON", "IF DAY---- $totalSteps - $previousTotalSteps :: $currentWeekNumber $previousWeekNumber")

                    previousTotalSteps = totalSteps
                    previousDayOfMonth = currentDayOfMonth
                    hydrationViewModel.onDrankAmountUpdate(0)

                    lifecycleScope.launch {
                        Log.d("SENSORON", "COROUTINE IF---- $currentWeekNumber $previousWeekNumber")

                        saveData(STEPS_PREFERENCES, previousTotalSteps)
                        saveData(STEPS_DAY_PREFERENCES, currentDayOfMonth)
                        saveData(STEPS_WEEK_PREFERENCES, currentWeekNumber)

                        hydrationViewModel.numberGoal.value?.toFloat()?.let {
                            saveData(HYDRATION_TARGET, it)
                        }
                        hydrationViewModel.drankAmount.value?.toFloat()?.let {
                            saveData(DRANK_AMOUNT, it)
                        }
                    }
                }

//                }

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
    suspend fun saveData(key: String, value: Float) {
        val dataStoreKey = floatPreferencesKey(key)
        dataStore.edit { steps ->
            steps[dataStoreKey] = value
        }
    }

    suspend fun loadData(key: String): Float? {
        val dataStoreKey = floatPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            Log.d("SENSORON", "ONPAUSE---- $currentWeekNumber $previousWeekNumber")

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
            loadData(HYDRATION_TARGET)?.let {
                hydrationViewModel.onNumberGoalUpdate(it.toInt())
            }
            loadData(DRANK_AMOUNT)?.let {
                hydrationViewModel.onDrankAmountUpdate(it.toInt())
            }
            Log.d("SENSORON", "RESUMECO---- $previousWeekNumber $savedWeekNumber")

            updateStepsAndTimer()
        }
    }
}
