package com.example.touchgrass.ui.hydration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * HydrationViewModel purpose is to save, update and change livedata
 */
class HydrationViewModel {
    private val _numberGoal: MutableLiveData<Int> = MutableLiveData()
    val numberGoal: LiveData<Int> = _numberGoal

    private val _drankAmount: MutableLiveData<Int> = MutableLiveData()
    val drankAmount: LiveData<Int> = _drankAmount

    private val _itemsAmount: MutableLiveData<Int> = MutableLiveData()
    val itemsAmount: LiveData<Int> = _itemsAmount

    /**
     * update the targeted goal to drink
     */
    fun onNumberGoalUpdate(numberGoal: Int) {
        _numberGoal.value = numberGoal
    }

    /**
     * add the consume amount of liquid
     */
    fun onDrankAmountPlus(drankAmount: Int) {
        _drankAmount.value = _drankAmount.value?.plus(drankAmount)
    }

    /**
     * load the drank amount to livedata
     */
    fun onDrankAmountUpdate(drankAmount: Int) {
        _drankAmount.value = drankAmount
    }

    /**
     * update the item amount made
     */
    fun onItemsAmountUpdate(itemsAmount: Int) {
        _itemsAmount.value = itemsAmount
    }

    /**
     * reduce item amount by 1
     */
    fun onItemsAmountReduce() {
        _itemsAmount.value = _itemsAmount.value?.minus(1)
    }
}