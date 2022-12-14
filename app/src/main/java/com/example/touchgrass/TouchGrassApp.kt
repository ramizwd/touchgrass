package com.example.touchgrass

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.touchgrass.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.touchgrass.utils.Constants.NOTIFICATION_CHANNEL_NAME

/**
 * Application class.
 */
class TouchGrassApp: Application() {
    /**
     * Creates a notification channel with high importance
     * for the steps counter service.
     */
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}