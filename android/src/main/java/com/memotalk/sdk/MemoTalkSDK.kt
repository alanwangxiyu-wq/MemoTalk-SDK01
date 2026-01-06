package com.memotalk.sdk

import android.content.Context
import java.io.File

object MemoTalkSDK {

    private var instance: MemoTalkSDKImpl? = null

    fun initialize(context: Context, config: SDKConfig): Result<Unit, PlaudError> {
        return try {
            instance = MemoTalkSDKImpl(context, config)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(PlaudError(0, "SDK initialization failed: ${e.message}"))
        }
    }

    fun getInstance(): MemoTalkSDKImpl? = instance

    fun getDeviceManager(): DeviceManager? = instance?.deviceManager
    fun getRecordingManager(): RecordingManager? = instance?.recordingManager
    fun getFileManager(): FileManager? = instance?.fileManager
    fun getOTAManager(): OTAManager? = instance?.otaManager
    fun getTranscriptionManager(): TranscriptionManager? = instance?.transcriptionManager

    fun release() {
        instance?.release()
        instance = null
    }
}

class MemoTalkSDKImpl(
    private val context: Context,
    private val config: SDKConfig
) {

    val deviceManager: DeviceManager
    val recordingManager: RecordingManager
    val fileManager: FileManager
    val otaManager: OTAManager
    val transcriptionManager: TranscriptionManager

    private val bleClient: BleClient

    init {
        bleClient = BleClient(context)
        deviceManager = DeviceManager(context)
        recordingManager = RecordingManager(bleClient)
        fileManager = FileManager(bleClient, context.cacheDir)
        otaManager = OTAManager(bleClient)
        transcriptionManager = TranscriptionManager(bleClient)
    }

    fun release() {
        bleClient.close()
    }
}
