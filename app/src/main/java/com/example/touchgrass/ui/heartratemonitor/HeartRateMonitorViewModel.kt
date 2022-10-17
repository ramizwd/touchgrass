package com.example.touchgrass.ui.heartratemonitor

import android.app.Application
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap

/**
 * ViewModel class for updating BLE values.
 * [results] holds all the unique devices found.
 * [scanResults] holds all the devices found in a list of ScanResult.
 * [btScanning] indicates whether or not BLE is scanning for devices.
 * [mBPM] hold the current value of the hear rate.
 * [writing] checks if devices is connected and reading new data from it.
 * [gattConnection] indicates if the BLE is connected to a device.
 * [secondsData] increase each time the BPM value updates providing data for the graph's x axis.
 */
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

    private val _secondsData = MutableLiveData(0f)
    val secondsData: LiveData<Float> = _secondsData

    /**
     * Updates the [secondsData] LiveData.
     */
    fun onSecondsUpdate(seconds: Float) {
        _secondsData.postValue(seconds)
    }

    /**
     * Updates the [mBPM] LiveData.
     */
    fun onBPMUpdate(mBPM: Int) {
        _mBPM.postValue(mBPM)
    }

    /**
     * Updates the [writing] LiveData.
     */
    fun onWritingUpdate(writing: Boolean) {
        _writing.postValue(writing)
    }

    /**
     * Updates the [gattConnection] LiveData.
     */
    fun onGattConnUpdate(gattConnection: Boolean) {
        _gattConnection.value = gattConnection
    }

    /**
     * Scans for devices by calling [leScanCallback] in a ViewModel scope,
     * updates [_scanResults] with the newly found list of devices
     * and updates [_btScanning] to true.
     */
    fun scanDevices(scanner: BluetoothLeScanner?) {
        viewModelScope.launch(Dispatchers.IO) {
            _btScanning.postValue(true)
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build()
            try {
                scanner?.startScan(null, settings, leScanCallback)
                delay(SCAN_PERIOD)
                scanner?.stopScan(leScanCallback)
                _scanResults.postValue(results.values.toList())
                _btScanning.postValue(false)
            } catch (e: SecurityException) {
                Log.d("DBG", "SecurityException")
                _btScanning.postValue(false)
            }
        }
        _scanResults.postValue(results.values.toList())
    }

    companion object GattAttributes {
        const val SCAN_PERIOD: Long = 3000
    }

    /**
     * Callback function for finding BLE devices with
     * HashMap to remove duplicate devices.
     */
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val deviceAddress = device.address
            results[deviceAddress] = result
        }
    }
}