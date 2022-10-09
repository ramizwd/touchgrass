package com.example.touchgrass.ui.stepcounter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.touchgrass.data.database.StepsGraph
import com.example.touchgrass.data.database.StepsGraphDB
import kotlinx.coroutines.launch

class StepsGraphViewModel(application: Application): AndroidViewModel(application) {
    private val graphDB = StepsGraphDB.get(application)

    fun getAllGraphEntries(): LiveData<List<StepsGraph>> =
        graphDB.stepsGraphDao().getAllGraphEntries()

    fun insertEntry(graphEntry: StepsGraph) {
        viewModelScope.launch {
            graphDB.stepsGraphDao().insertGraphEntry(graphEntry)
        }
    }

    fun deleteEntries() {
        viewModelScope.launch {
            graphDB.stepsGraphDao().deleteAllEntries()
        }
    }
}