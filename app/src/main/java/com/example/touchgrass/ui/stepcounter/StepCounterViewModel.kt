package com.example.touchgrass.ui.stepcounter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepCounterViewModel: ViewModel() {
    private val _steps: MutableLiveData<Float> = MutableLiveData()
    val steps: LiveData<Float> = _steps

    fun onStepsUpdate(steps: Float) {
        _steps.value = steps
        Log.d("StepCounter", "$steps")

    }
}