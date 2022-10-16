package com.example.touchgrass.ui.stepcounter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.touchgrass.data.database.StepsGraph
import com.example.touchgrass.data.database.StepsGraphDB
import kotlinx.coroutines.launch

/**
 * ViewModel class that communicates with the [StepsGraphDB] database.
 */
class StepsGraphViewModel(application: Application): AndroidViewModel(application) {
    private val graphDB = StepsGraphDB.get(application)

    /**
     * Gets all the values stores in the database.
     */
    fun getAllGraphEntries(): LiveData<List<StepsGraph>> =
        graphDB.stepsGraphDao().getAllGraphEntries()

    /**
     * Insets new data into the database.
     */
    fun insertEntry(graphEntry: StepsGraph) {
        viewModelScope.launch {
            graphDB.stepsGraphDao().insertGraphEntry(graphEntry)
        }
    }

    /**
     * Deletes everything stored in the the database.
     */
    fun deleteEntries() {
        viewModelScope.launch {
            graphDB.stepsGraphDao().deleteAllEntries()
        }
    }
}