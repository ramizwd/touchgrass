package com.example.touchgrass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
//        previousTotalSteps = totalSteps
//        previousDayOfMonth = currentDayOfMonth




        Log.d("ALARMHERE", "HERRROOO---------")
    }

}