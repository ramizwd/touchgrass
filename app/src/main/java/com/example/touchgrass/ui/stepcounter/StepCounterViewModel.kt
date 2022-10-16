package com.example.touchgrass.ui.stepcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel class with [currentSteps] LiveData that provides the current total steps.
 * [targetStepsIndex] the index value from the dropdown menu of the selected target steps.
 * [targetStepsValue] the set target steps value.
 */
class StepCounterViewModel: ViewModel() {
    private val _steps: MutableLiveData<Int> = MutableLiveData()
    val currentSteps: LiveData<Int> = _steps

    private val _targetStepsIndex: MutableLiveData<Float> = MutableLiveData()
    val targetStepsIndex: LiveData<Float> = _targetStepsIndex

    private val _targetStepsValue: MutableLiveData<Float> = MutableLiveData()
    val targetStepsValue: LiveData<Float> = _targetStepsValue

    /**
     * Updates the value of the [currentSteps] LiveData.
     */
    fun onStepsUpdate(steps: Int) {
        _steps.value = steps
    }

    /**
     * Updates the value of [targetStepsIndex] LiveData.
     */
    fun onTargetStepsIndexUpdate(targetStepsIndex: Float) {
        _targetStepsIndex.value = targetStepsIndex
    }

    /**
     * Updates the value of [targetStepsValue] LiveData.
     */
    fun onTargetStepsValueUpdate(targetStepsValue: Float) {
        _targetStepsValue.value = targetStepsValue
    }
}