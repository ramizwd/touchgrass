package com.example.touchgrass.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data access object for the graph database interactions.
 */
@Dao
interface StepsGraphDao {

    // Read all inserted data from the database table
    @Query("SELECT * FROM stepsGraph")
    fun getAllGraphEntries(): LiveData<List<StepsGraph>>

    // Insert new data to the database, replace if duplicate
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGraphEntry(graphEntries: StepsGraph): Long

    // Delete all the data stored in the database
    @Query("DELETE FROM stepsGraph")
    suspend fun deleteAllEntries()
}