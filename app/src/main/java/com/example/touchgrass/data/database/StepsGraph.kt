package com.example.touchgrass.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database table for the steps counter graph.
 */
@Entity
data class StepsGraph(
    @PrimaryKey
    val dayOfWeek: Float,
    val steps: Float,
)
