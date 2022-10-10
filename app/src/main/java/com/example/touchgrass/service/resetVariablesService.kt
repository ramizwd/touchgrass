package com.example.touchgrass.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.touchgrass.*
import kotlinx.coroutines.*

class resetVariablesService: Service() {
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            StepCounterService.ACTION_START -> start()
            StepCounterService.ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
//        previousTotalSteps = totalSteps
//        previousDayOfMonth = currentDayOfMonth

    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}