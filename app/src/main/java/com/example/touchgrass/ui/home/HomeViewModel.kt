package com.example.touchgrass.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private val _minute: MutableLiveData<Int> = MutableLiveData()
    val currentTotalMinutes: LiveData<Int> = _minute

    fun onHourUpdate(totalMinutesOfDay: Int) {
        _minute.value = totalMinutesOfDay
    }
}