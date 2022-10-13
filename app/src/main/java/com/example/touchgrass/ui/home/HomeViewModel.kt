package com.example.touchgrass.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private val _minute: MutableLiveData<Int> = MutableLiveData()
    val currentTotalMinutes: LiveData<Int> = _minute

    private val _streak: MutableLiveData<Float> = MutableLiveData()
    val streak: LiveData<Float> = _streak

    fun onHourUpdate(totalMinutesOfDay: Int) {
        _minute.value = totalMinutesOfDay
    }
    fun onStreaksUpdate(streak: Float) {
        _streak.value = streak
    }
}