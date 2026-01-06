package com.memotalk.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.memotalk.sdk.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var deviceManager: DeviceManager
    private lateinit var recordingManager: RecordingManager
    private var selectedDevice: Device? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SDK
        val config = SDKConfig(
            appKey = "your-app-key",
            appSecret = "your-app-secret",
            logLevel = LogLevel.DEBUG
        )

        val initResult = MemoTalkSDK.initialize(this, config)
        when (initResult) {
            is Result.Success -> {
                deviceManager = MemoTalkSDK.getDeviceManager()!!
                recordingManager = MemoTalkSDK.getRecordingManager()!!
            }
            is Result.Failure -> {
                // Handle initialization error
            }
        }

        // Request permissions
        requestPermissions()

        setContent {
            MemoTalkDemoTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        var devices by remember { mutableStateOf<List<Device>>(emptyList()) }
        var isScanning by remember { mutableStateOf(false) }
        var isRecording by remember { mutableStateOf(false) }
        var status by remember { mutableStateOf("Ready") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "MemoTalk SDK Demo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Scan Button
            Button(
                onClick = {
                    lifecycleScope.launch {
                        isScanning = true
                        status = "Scanning..."
                        val result = deviceManager.startScan()
                        when (result) {
                            is Result.Success -> {
                                devices = result.value
                                status = "Found ${devices.size} devices"
                            }
                            is Result.Failure -> {
                                status = "Scan failed: ${result.error.message}"
                            }
                        }
                        isScanning = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isScanning
            ) {
                Text(if (isScanning) "Scanning..." else "Scan Devices")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Device List
            Text("Available Devices:", style = MaterialTheme.typography.titleMedium)
            devices.forEach { device ->
                Button(
                    onClick = {
                        selectedDevice = device
                        lifecycleScope.launch {
                            val result = deviceManager.connect(device)
                            when (result) {
                                is Result.Success -> {
                                    status = "Connected to ${device.name}"
                                }
                                is Result.Failure -> {
                                    status = "Connection failed: ${result.error.message}"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("${device.name} (${device.address})")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recording Controls
            if (selectedDevice != null) {
                Text("Recording Controls:", style = MaterialTheme.typography.titleMedium)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            lifecycleScope.launch {
                                val result = recordingManager.startRecording(selectedDevice!!)
                                when (result) {
                                    is Result.Success -> {
                                        isRecording = true
                                        status = "Recording started"
                                    }
                                    is Result.Failure -> {
                                        status = "Start recording failed: ${result.error.message}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text("Start")
                    }

                    Button(
                        onClick = {
                            lifecycleScope.launch {
                                val result = recordingManager.stopRecording(selectedDevice!!)
                                when (result) {
                                    is Result.Success -> {
                                        isRecording = false
                                        status = "Recording stopped"
                                    }
                                    is Result.Failure -> {
                                        status = "Stop recording failed: ${result.error.message}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isRecording
                    ) {
                        Text("Stop")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Display
            Text("Status:", style = MaterialTheme.typography.titleMedium)
            Text(status, style = MaterialTheme.typography.bodyMedium)
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MemoTalkSDK.release()
    }
}

@Composable
private fun MemoTalkDemoTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}
