package com.example.touchgrass.ui.heartratemonitor

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.touchgrass.gattclient.GattClientCallback
import com.example.touchgrass.R

@Composable
fun HeartRateMonitorScreen(
    viewModel: HeartRateMonitorViewModel,
    bluetoothAdapter: BluetoothAdapter
) {

    HeartRateMonitorBody(
        viewModel = viewModel,
        bluetoothAdapter = bluetoothAdapter
    )
}

@Composable
fun HeartRateMonitorBody(
    viewModel: HeartRateMonitorViewModel,
    bluetoothAdapter: BluetoothAdapter
) {
    val context = LocalContext.current
    val btScanning: Boolean by viewModel.btScanning.observeAsState(false)
    val bpm by viewModel.mBPM.observeAsState()
    val writing by viewModel.writing.observeAsState()
    val isConnected by viewModel.gattConnection.observeAsState()
    var result: List<ScanResult>? = null

    val hr by viewModel.heartRateData.observeAsState()
    val sec by viewModel.secondsData.observeAsState()

    val gattClientCallback = GattClientCallback(viewModel)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
        PackageManager.PERMISSION_GRANTED
    ) {
        val results: List<ScanResult>? by viewModel.scanResults.observeAsState(null)
        result = results
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (bluetoothAdapter.isEnabled) {
                            viewModel.scanDevices(bluetoothAdapter.bluetoothLeScanner)
                        } else {
                            Toast.makeText(context,
                            context.getString(R.string.enable_bt_toast), Toast.LENGTH_LONG)
                                .show()
                        }
                    },
                    modifier = Modifier
                ) { Text(text = stringResource(R.string.scan_bt_btn)) }

                if (btScanning) {
                    Text(text = stringResource(R.string.scanning_bt_txt))
                } else {
                    if (result != null && result.isEmpty()) {
                        Text(text = stringResource(R.string.no_devices_found_bt))
                    }
                    Text(
                        text = if (writing == true)
                            stringResource(R.string.hr_bpm_txt, bpm ?: 0)
                        else if (isConnected == true)
                            stringResource(R.string.connected_bt) else "",
                        modifier = Modifier.padding(8.dp)
                    )
                    LazyColumn {
                        if (result != null) {
                            items(result,
                                key = { listItem -> listItem.device.address
                            }) { device ->
                                if (device.isConnectable) {
                                    Text(
                                        text = if (device.device.name != null)
                                            device.device.name
                                        else stringResource(R.string.unknown_bt_device),
                                        modifier = Modifier.selectable(
                                            selected = true,
                                            onClick = {
                                                gattClientCallback.connectToHRMonitor(device.device, context)
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column {
                HeartRateGraph(hr, sec)
            }
        }
    }
}