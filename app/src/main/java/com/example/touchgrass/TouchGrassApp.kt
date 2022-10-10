package com.example.touchgrass

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class TouchGrassApp: Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "steps",
            "Steps",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}