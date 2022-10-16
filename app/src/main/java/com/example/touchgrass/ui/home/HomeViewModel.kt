package com.example.touchgrass.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel class with [currentTotalMinutes] LiveData that provides the current total minutes of
 * the day and [streak] LiveData that provides the current streak count.
 */
class HomeViewModel: ViewModel() {
    private val _minute: MutableLiveData<Int> = MutableLiveData()
    val currentTotalMinutes: LiveData<Int> = _minute

    private val _streak: MutableLiveData<Float> = MutableLiveData()
    val streak: LiveData<Float> = _streak

    /**
     * Updates the [currentTotalMinutes] LiveData.
     */
    fun onTotalMinutesUpdate(totalMinutesOfDay: Int) {
        _minute.value = totalMinutesOfDay
    }

    /**
     * Updates the [streak] LiveData.
     */
    fun onStreaksUpdate(streak: Float) {
        _streak.value = streak
    }
}