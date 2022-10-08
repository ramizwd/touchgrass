package com.example.touchgrass.ui.heartratemonitor

import android.Manifest
import android.app.Application
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap

class HeartRateMonitorViewModel(application: Application) : AndroidViewModel(application) {
    private val results = HashMap<String, ScanResult>()

    private val _scanResults = MutableLiveData<List<ScanResult>>(null)
    val scanResults:  LiveData<List<ScanResult>> = _scanResults

    private val _btScanning = MutableLiveData(false)
    val btScanning: LiveData<Boolean> = _btScanning

    private val _mBPM = MutableLiveData(0)
    val mBPM: LiveData<Int> = _mBPM

    private val _writing = MutableLiveData(false)
    val writing: LiveData<Boolean> = _writing

    private val _gattConnection = MutableLiveData(false)
    val gattConnection: LiveData<Boolean> = _gattConnection


    private val _heartRateData = MutableLiveData<Float>()
    val heartRateData: LiveData<Float> = _heartRateData

    private val _secondsData = MutableLiveData(0f)
    val secondsData: LiveData<Float> = _secondsData

    fun onHeartRateUpdate(heartRateData: Float) {
        _heartRateData.postValue(heartRateData)
    }

    fun onSecondsUpdate(seconds: Float) {
        _secondsData.postValue(seconds)
    }

    fun onBPMUpdate(mBPM: Int) {
        _mBPM.postValue(mBPM)
    }

    fun onWritingUpdate(writing: Boolean) {
        _writing.postValue(writing)
    }

    fun onGattConnUpdate(gattConnection: Boolean) {
        _gattConnection.value = gattConnection
    }

    fun scanDevices(scanner: BluetoothLeScanner?) {
        viewModelScope.launch(Dispatchers.IO) {
            _btScanning.postValue(true)
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build()
            if (ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                scanner?.startScan(null, settings, leScanCallback)
                delay(SCAN_PERIOD)
                scanner?.stopScan(leScanCallback)
                _scanResults.postValue(results.values.toList())
                _btScanning.postValue(false)
            }


        }
        _scanResults.postValue(results.values.toList())
    }

    companion object GattAttributes {
        const val SCAN_PERIOD: Long = 3000
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val deviceAddress = device.address
            results[deviceAddress] = result
        }
    }
}