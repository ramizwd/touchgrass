package com.example.touchgrass.ui.stepcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepCounterViewModel: ViewModel() {
    private val _steps: MutableLiveData<Int> = MutableLiveData()
    val currentSteps: LiveData<Int> = _steps

    private val _targetStepsIndex: MutableLiveData<Float> = MutableLiveData()
    val targetStepsIndex: LiveData<Float> = _targetStepsIndex

    fun onStepsUpdate(steps: Int) {
        _steps.value = steps
    }

    fun onTargetStepsIndexUpdate(targetStepsIndex: Float) {
        _targetStepsIndex.value = targetStepsIndex
    }
}