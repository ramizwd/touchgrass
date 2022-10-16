package com.example.touchgrass.ui.heartratemonitor

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.navigation.NavController
import com.example.touchgrass.R
import com.example.touchgrass.gattclient.GattClientCallback
import com.example.touchgrass.ui.shared.components.CircularProgressBar
import com.example.touchgrass.ui.theme.SCBlue
import com.example.touchgrass.ui.theme.SCOrange
import com.example.touchgrass.ui.theme.SCWhite
import com.example.touchgrass.ui.theme.SCYellow

@Composable
fun HeartRateMonitorScreen(
    viewModel: HeartRateMonitorViewModel,
    bluetoothAdapter: BluetoothAdapter?,
    navController: NavController,
) {
    val btScanning: Boolean by viewModel.btScanning.observeAsState(false)
    val bpm by viewModel.mBPM.observeAsState()
    val writing by viewModel.writing.observeAsState()
    val isConnected by viewModel.gattConnection.observeAsState()

    val heartRate by viewModel.heartRateData.observeAsState()
    val seconds by viewModel.secondsData.observeAsState()

    val gattClientCallback = GattClientCallback(viewModel)

    HeartRateMonitorBody(
        viewModel = viewModel,
        bluetoothAdapter = bluetoothAdapter,
        btScanning = btScanning,
        bpm = bpm,
        writing = writing,
        isConnected = isConnected,
        heartRate = heartRate,
        seconds = seconds,
        gattClientCallback = gattClientCallback,
        navController = navController,
    )
}

@Composable
fun HeartRateMonitorBody(
    viewModel: HeartRateMonitorViewModel,
    bluetoothAdapter: BluetoothAdapter?,
    btScanning: Boolean,
    bpm: Int?,
    writing: Boolean?,
    isConnected: Boolean?,
    heartRate: Float?,
    seconds: Float?,
    gattClientCallback: GattClientCallback,
    navController: NavController,
) {

    val context = LocalContext.current
    var result: List<ScanResult>? = null

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
        PackageManager.PERMISSION_GRANTED
    ) {
        val results: List<ScanResult>? by viewModel.scanResults.observeAsState(null)
        result = results
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT
            ), 1
        )
    } else {
        requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hr_monitor)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow_ic_desc)
                        )

                    }
                },
                actions = {
                    if (btScanning) {
                        CircularProgressIndicator(color = SCWhite)
                    } else {
                        Text(
                            text = stringResource(R.string.scan),
                            modifier = Modifier
                                .padding(12.dp)
                                .selectable(
                                    selected = true,
                                    onClick = {
                                        if (bluetoothAdapter == null) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    context.getString(R.string.device_does_not_support_bt),
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                        if (bluetoothAdapter?.isEnabled == true) {
                                            viewModel.scanDevices(bluetoothAdapter.bluetoothLeScanner)
                                            viewModel.onWritingUpdate(false)
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    context.getString(R.string.enable_bt_toast),
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }
                                )
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .weight(3f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressBar(
                            value = (bpm ?: 0) / 120.toFloat(),
                            target = 120,
                            isHeartRateScreen = true,
                            foregroundColor =
                            when (bpm ?: 0) {
                                in 0..60 -> SCBlue
                                in 61..100 -> SCYellow
                                else -> SCOrange
                            },
                            writing = writing ?: false,
                            isConnected = isConnected ?: false
                        )
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
                    if ((result != null) && (writing == false)) {
                        if (!btScanning) {
                            if (result.isEmpty()) {
                                Text(text = stringResource(R.string.no_devices_found_bt))
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.4f)
                        ) {
                            if (!btScanning) {
                                items(result,
                                    key = { listItem ->
                                        listItem.device.address
                                    }) { device ->
                                    if (device.isConnectable) {
                                        TextButton(onClick = {
                                            gattClientCallback.connectToHRMonitor(
                                                device.device,
                                                context
                                            )
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_bluetooth),
                                                contentDescription = stringResource(R.string.bluetooth_ic_desc)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Text(
                                                text = if (device.device.name != null)
                                                    device.device.name
                                                else stringResource(R.string.unknown_bt_device),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        HeartRateGraph(heartRate, seconds)
                    }
                }
            }
        }
    }
}