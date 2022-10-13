package com.example.touchgrass.utils

object Constants {
    // Service actions
    const val ACTION_START_SERVICE = "ACTION_START"
    const val ACTION_STOP_SERVICE = "ACTION_STOP"

    // Service notification
    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID = "steps"
    const val NOTIFICATION_CHANNEL_NAME = "Steps"

    // Tags
    const val SENSOR_STEPS_TAG = "StepCounter"

    // Preferences DataStore
    const val STEPS_PREFERENCES = "steps"
    const val STEPS_DAY_PREFERENCES = "step_counter_day"
    const val STEPS_WEEK_PREFERENCES = "step_counter_week"
    const val STEPS_TARGET_PREFERENCES = "step_target"
    const val DRANK_AMOUNT_PREFERENCES = "drank_amount"
    const val HYDRATION_TARGET_PREFERENCES = "number_goal"
}