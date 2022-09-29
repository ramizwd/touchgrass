package com.example.touchgrass.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private val _hour: MutableLiveData<Int> = MutableLiveData()
    val currentHour: LiveData<Int> = _hour

    fun onHourUpdate(hour: Int) {
        _hour.value = hour

//            when(hour) {
//            0 -> 0
//            13 -> 10
//            14 -> 20
//            15 -> 30
//            16 -> 40
//            17 -> 50
//            18 -> 60
//            19 -> 70
//            20 -> 80
//            21 -> 90
//            22 -> 95
//            23 -> 99
//            else -> 0
//        }
    }
}