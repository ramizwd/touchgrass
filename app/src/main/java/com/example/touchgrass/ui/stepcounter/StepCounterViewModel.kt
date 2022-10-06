package com.example.touchgrass.ui.stepcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepCounterViewModel: ViewModel() {
    private val _steps: MutableLiveData<Int> = MutableLiveData()
    val currentSteps: LiveData<Int> = _steps

    private val _targetStepsIndex: MutableLiveData<Float> = MutableLiveData()
    val targetStepsIndex: LiveData<Float> = _targetStepsIndex

    private val _dayOfWeek: MutableLiveData<Int> = MutableLiveData()
    val dayOfWeek: LiveData<Int> = _dayOfWeek

    fun onStepsUpdate(steps: Int) {
        _steps.value = steps
    }

    fun onTargetStepsIndexUpdate(targetStepsIndex: Float) {
        _targetStepsIndex.value = targetStepsIndex
    }

    fun onDayUpdate(dayOfWeek: Int) {
        _dayOfWeek.value = dayOfWeek
    }
}