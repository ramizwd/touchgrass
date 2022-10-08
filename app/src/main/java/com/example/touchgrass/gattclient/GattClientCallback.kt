package com.example.touchgrass.gattclient

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import java.util.*

class GattClientCallback(model: HeartRateMonitorViewModel) : BluetoothGattCallback() {
    companion object {
        val HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D)
        val HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37)
        val CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902)
        private const val TAG = "BTDebug"

        private fun convertFromInteger(i: Int): UUID {
            val msb = 0x0000000000001000L
            val lsb = -0x7fffff7fa064cb05L
            val value = (i and -0x1).toLong()
            return UUID(msb or (value shl 32), lsb)
        }
    }

    private val viewModel = model
    private var sec = 0f

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (status == BluetoothGatt.GATT_FAILURE) {
            Log.d(TAG, "GATT connection failure")
            return
        } else if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "GATT connection success")
            return
        }
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(TAG, "Connected GATT service")
            try {
                viewModel.onGattConnUpdate(gatt.discoverServices())
                Log.d(TAG, "Connected GATT service try-catch")
            } catch (e: SecurityException) {
                Log.d(TAG, "SecurityException")
            }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d(TAG, "Disconnected GATT service")
            viewModel.onGattConnUpdate(false)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "not successful")
            return
        }

        Log.d(TAG, "onServicesDiscovered")
        for (gattService in gatt.services) {
            Log.d(TAG, "$gattService")

            if (gattService.uuid == HEART_RATE_SERVICE_UUID) {
                Log.d(TAG, "Heart Rate Service found")

                for (gattCharacteristic in gattService.characteristics)
                    Log.d(TAG, "Characteristic ${gattCharacteristic.uuid}")

                try {
                    val characteristic = gatt.getService(HEART_RATE_SERVICE_UUID)
                        .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)
                    gatt.setCharacteristicNotification(characteristic, true)

                    if (gatt.setCharacteristicNotification(characteristic, true)) {
                        val descriptor =
                            characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        val writing = gatt.writeDescriptor(descriptor)
                        viewModel.onWritingUpdate(writing)
                    }
                } catch (e: SecurityException) {
                    Log.d(TAG, "SecurityException")
                }
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        Log.d(TAG, "onDescriptorWrite")
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        val bpm = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
        Log.d(TAG, "BPM: $bpm")
        viewModel.onBPMUpdate(bpm)
        viewModel.onHeartRateUpdate(bpm.toFloat())
        viewModel.onSecondsUpdate(sec++)

    }

    fun connectToHRMonitor(device: BluetoothDevice, context: Context) {
        val connectGatt = device.connectGatt(context, false, this@GattClientCallback)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            connectGatt.device.createBond()
            this@GattClientCallback.onConnectionStateChange(
                connectGatt,
                connectGatt.device.bondState,
                BluetoothGatt.STATE_CONNECTED
            )
            this@GattClientCallback.onServicesDiscovered(connectGatt, connectGatt.device.bondState)
        }
    }
}

