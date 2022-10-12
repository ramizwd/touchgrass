package com.example.touchgrass.service

import android.content.Context
import android.content.Intent

object StepCounterServiceHelper {
    fun launchForegroundService(context: Context, action: String) {
        Intent(context, StepCounterService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}