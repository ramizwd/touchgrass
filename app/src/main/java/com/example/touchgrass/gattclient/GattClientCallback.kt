package com.example.touchgrass.gattclient

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import com.example.touchgrass.utils.Constants.BLE_TAG
import java.util.*

/**
 * GATT client class that connects to the GATT server.
 */
class GattClientCallback(model: HeartRateMonitorViewModel): BluetoothGattCallback() {
    companion object {
        val HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D)
        val HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37)
        val CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902)

        /**
         * Converts integers to the full UUIDs.
         */
        private fun convertFromInteger(i: Int): UUID {
            val msb = 0x0000000000001000L
            val lsb = -0x7fffff7fa064cb05L
            val value = (i and -0x1).toLong()
            return UUID(msb or (value shl 32), lsb)
        }
    }

    private val viewModel = model
    private var sec = 0f

    /**
     * If the connection is established this function with STATE_CONNECTED is called.
     */
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (status == BluetoothGatt.GATT_FAILURE) {
            Log.d(BLE_TAG, "GATT connection failure")
            return
        } else if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(BLE_TAG, "GATT connection success")
            return
        }
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(BLE_TAG, "Connected GATT service")
            try {
                viewModel.onGattConnUpdate(gatt.discoverServices())
                Log.d(BLE_TAG, "Connected GATT service try-catch")
            } catch (e: SecurityException) {
                Log.d(BLE_TAG, "SecurityException")
            }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d(BLE_TAG, "Disconnected GATT service")
            viewModel.onGattConnUpdate(false)
        }
    }

    /**
     * Invoked when a new services have been discovered.
     */
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d(BLE_TAG, "not successful")
            return
        }

        Log.d(BLE_TAG, "onServicesDiscovered")
        for (gattService in gatt.services) {
            Log.d(BLE_TAG, "$gattService")

            if (gattService.uuid == HEART_RATE_SERVICE_UUID) {
                Log.d(BLE_TAG, "Heart Rate Service found")

                for (gattCharacteristic in gattService.characteristics)
                    Log.d(BLE_TAG, "Characteristic ${gattCharacteristic.uuid}")

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
                    Log.d(BLE_TAG, "SecurityException")
                }
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        Log.d(BLE_TAG, "onDescriptorWrite")
    }

    /**
     * Updates the LiveData in the ViewModel each time the characteristic's content changes.
     */
    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        val bpm = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
        Log.d(BLE_TAG, "BPM: $bpm")
        viewModel.onBPMUpdate(bpm)
        viewModel.onSecondsUpdate(sec++)

    }

    /**
     * Makes a connection to the BLE device.
     */
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

