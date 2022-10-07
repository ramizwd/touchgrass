package com.example.touchgrass.ui.hydration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HydrationViewModel {
    private val _numberGoal: MutableLiveData<Int> = MutableLiveData()
    val numberGoal: LiveData<Int> = _numberGoal

    private var _drankAmount: MutableLiveData<Int> = MutableLiveData()
    val drankAmount: LiveData<Int> = _drankAmount
    private var _itemsAmount: MutableLiveData<Int> = MutableLiveData()
    val itemsAmount: LiveData<Int> = _itemsAmount

    fun onNumberGoalUpdate(numberGoal: Int) {
        _numberGoal.value = numberGoal
    }

    fun onDrankAmountPlus(drankAmount: Int) {
        _drankAmount.value = _drankAmount.value?.plus(drankAmount)
    }

    fun onDrankAmountUpdate(drankAmount: Int) {
        _drankAmount.value = drankAmount
    }

    fun onItemsAmountUpdate(itemsAmount: Int) {
        _itemsAmount.value = itemsAmount
    }

    fun onItemsAmountReduce() {
        _itemsAmount.value = _itemsAmount.value?.minus(1)
    }
}