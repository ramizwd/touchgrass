package com.example.touchgrass.ui.hydration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HydrationViewModel {
    private val _numberGoal: MutableLiveData<Int> = MutableLiveData()
    val numberGoal: LiveData<Int> = _numberGoal
    private var _drankAmount: MutableLiveData<Int> = MutableLiveData()
    val drankAmount: LiveData<Int> = _drankAmount

    fun onNumberGoalUpdate(numberGoal: Int) {
        _numberGoal.value = numberGoal
    }

    fun onDrankAmountUpdate(drankAmount: Int) {
        _drankAmount.value = drankAmount
    }



}