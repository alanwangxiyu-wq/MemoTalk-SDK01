package com.memotalk.sdk

import android.bluetooth.BluetoothDevice

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class Device( 
    val name: String?,
    val address: String,
    val rssi: Int,
    val nativeDevice: BluetoothDevice,
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED
)

data class Recording(
    val fileId: String,
    val duration: Long,
    val timestamp: Long,
    val fileSize: Long,
    var isSynced: Boolean = false
)

data class DeviceStatus(
    val batteryLevel: Int,
    val storageFree: Long,
    val storageTotal: Long,
    val isRecording: Boolean
)

data class SDKConfig(
    val appKey: String,
    val appSecret: String,
    val logLevel: LogLevel = LogLevel.INFO
)

enum class LogLevel {
    VERBOSE, INFO, DEBUG, WARN, ERROR
}

data class PlaudError(
    val code: Int,
    val message: String
)
