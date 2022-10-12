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
import com.example.touchgrass.utils.Constants.SENSOR_STEPS_TAG

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START_SERVICE -> start()
            ACTION_STOP_SERVICE -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Counting your steps...")
            .setSmallIcon(R.drawable.ic_walking)
            .setSilent(true)
            .setOngoing(true)

        registerStepCounterSensor()
        startForeground(NOTIFICATION_ID, notification.build())
    }

    private fun stop() {
        unregisterStepCounterSensor()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

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

    private fun unregisterStepCounterSensor() {
        if (isSensorOn) {
            sensorManager.unregisterListener(this)
            isSensorOn = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        event.values.firstOrNull()?.let {
            Log.d(SENSOR_STEPS_TAG, "Count: $it")
            totalSteps = it
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

}