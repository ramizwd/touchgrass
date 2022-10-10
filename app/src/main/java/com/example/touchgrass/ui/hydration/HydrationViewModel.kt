package com.example.touchgrass.ui.hydration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HydrationViewModel {
    private val _numberGoal: MutableLiveData<Int> = MutableLiveData()
    val numberGoal: LiveData<Int> = _numberGoal

    private var _drankAmount: MutableLiveData<Int> = MutableLiveData()
    val drankAmount: LiveData<Int> = _drankAmount

    private var _buttonsAmount: MutableLiveData<Int> = MutableLiveData()
    val buttonsAmount: LiveData<Int> = _buttonsAmount

    fun onNumberGoalUpdate(numberGoal: Int) {
        _numberGoal.value = numberGoal
    }

    fun onDrankAmountPlus(drankAmount: Int) {
        _drankAmount.value = _drankAmount.value?.plus(drankAmount)
    }

    fun onDrankAmountUpdate(drankAmount: Int) {
        _drankAmount.value = drankAmount
    }

    fun onButtonsAmountUpdate(buttonAmounts: Int) {
        _buttonsAmount.value = buttonAmounts
    }

    fun onItemsAmountReduce() {
        _buttonsAmount.value = _buttonsAmount.value?.minus(1)
    }
}