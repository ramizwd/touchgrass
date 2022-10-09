package com.example.touchgrass.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepsGraphDao {
    @Query("SELECT * FROM stepsGraph")
    fun getAllGraphEntries(): LiveData<List<StepsGraph>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGraphEntry(graphEntries: StepsGraph): Long

    @Query("DELETE FROM stepsGraph")
    suspend fun deleteAllEntries()
}