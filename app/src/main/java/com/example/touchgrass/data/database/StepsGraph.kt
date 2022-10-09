package com.example.touchgrass.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepsGraph(
    @PrimaryKey
    val dayOfWeek: Float,
    val steps: Float,
)
