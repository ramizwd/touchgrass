package com.example.touchgrass.ui.stepcounter

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepCounterViewModel: ViewModel() {
    private val _steps: MutableLiveData<Int> = MutableLiveData()
    val currentSteps: LiveData<Int> = _steps

    /* TODO reset counter functionality
    private val _resetCounter: MutableLiveData<Boolean> = MutableLiveData()
    val resetCounter: LiveData<Boolean> = _resetCounter

    var reCounter by mutableStateOf(false)

    fun onResetCounter() {
        reCounter = true
    }
    */

    fun onStepsUpdate(steps: Int) {
        _steps.value = steps
    }
}