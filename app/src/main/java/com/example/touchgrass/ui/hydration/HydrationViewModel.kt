package com.example.touchgrass.ui.hydration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HydrationViewModel {
    private val _numberGoal: MutableLiveData<Int> = MutableLiveData()
    val numberGoal: LiveData<Int> = _numberGoal
    private var _itemAmount: MutableLiveData<Int> = MutableLiveData()
    val itemAmount: LiveData<Int> = _itemAmount
    private var _drankAmount: MutableLiveData<Int> = MutableLiveData()
    val drankAmount: LiveData<Int> = _drankAmount
    private var _liquidAmount: MutableLiveData<Int> = MutableLiveData()
    val liquidAmount: LiveData<Int> = _liquidAmount

    fun onNumberGoalUpdate(numberGoal: Int) {
        _numberGoal.value = numberGoal
    }

    fun onItemAmountUpdate(itemAmount: Int) {
        _itemAmount.value = itemAmount
    }

    fun onDrankAmountUpdate(drankAmount: Int) {
        _drankAmount.value = drankAmount
    }

    fun onLiquidAmountUpdate(liquidAmount: Int) {
        _liquidAmount.value = liquidAmount
    }
}