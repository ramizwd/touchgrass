package com.example.touchgrass.service

import android.content.Context
import android.content.Intent

/**
 * Helper object for the steps counter service.
 */
object StepCounterServiceHelper {
    /**
     * Launches the service and sends an intent with an action to start or stop the service.
     */
    fun launchForegroundService(context: Context, action: String) {
        Intent(context, StepCounterService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}