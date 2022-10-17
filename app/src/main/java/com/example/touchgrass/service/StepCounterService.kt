package com.example.touchgrass.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.touchgrass.R
import com.example.touchgrass.utils.Constants.ACTION_START_SERVICE
import com.example.touchgrass.utils.Constants.ACTION_STOP_SERVICE
import com.example.touchgrass.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.touchgrass.utils.Constants.NOTIFICATION_ID
import com.example.touchgrass.utils.Constants.STEPS_SENSOR_TAG

/**
 * Foreground service class with SensorEventListener for the steps counter sensor.
 */
class StepCounterService: Service(), SensorEventListener {
    companion object {
        private lateinit var sensorManager: SensorManager
        private var stepCounter: Sensor? = null
        var totalSteps = 0f
        var isSensorOn by mutableStateOf(false)
            private set
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     * Check what the intent action is and launch a function depending on it.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START_SERVICE -> start()
            ACTION_STOP_SERVICE -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Creates the notification, starts the foreground service,
     * and launches [registerStepCounterSensor] function.
     */
    private fun start() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notif_message))
            .setSmallIcon(R.drawable.ic_walking)
            .setSilent(true)
            .setOngoing(true)

        registerStepCounterSensor()
        startForeground(NOTIFICATION_ID, notification.build())
    }

    /**
     * Remove the service from the foreground state, stops the service,
     * launches [unregisterStepCounterSensor] function.
     */
    private fun stop() {
        unregisterStepCounterSensor()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * Register the steps counter sensor manager if the sensor is available,
     * else make a toast and launch the [stop] function.
     */
    private fun registerStepCounterSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounter?.also {
                sensorManager.registerListener(
                    this,
                    it,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }
            isSensorOn = true
        } else {
            Toast.makeText(this,
                getString(R.string.step_sensor_not_found),
                Toast.LENGTH_LONG).show()
            stop()
        }
    }

    /**
     * Unregister the steps counter sensor manager if the sensor is on.
     */
    private fun unregisterStepCounterSensor() {
        if (isSensorOn) {
            sensorManager.unregisterListener(this)
            isSensorOn = false
        }
    }

    /**
     * Updates the [totalSteps] variable with the steps value
     * every time the sensor detects a change.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        event.values.firstOrNull()?.let {
            Log.d(STEPS_SENSOR_TAG, "Count: $it")
            totalSteps = it
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

}