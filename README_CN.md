# MemoTalk SDK

**将MemoTalk智能语音录音和AI能力集成到您应用的官方SDK**

[![版本](https://img.shields.io/badge/版本-1.0.0-blue.svg)](https://github.com/alanwangxiyu-wq/MemoTalk-SDK01)
[![平台](https://img.shields.io/badge/平台-Android%20%7C%20iOS-lightgrey.svg)](https://github.com/alanwangxiyu-wq/MemoTalk-SDK01)
[![许可证](https://img.shields.io/badge/许可证-MIT-green.svg)](https://opensource.org/licenses/MIT)

MemoTalk SDK提供了一套功能强大且易于使用的接口，用于与MemoTalk硬件设备交互。它处理了蓝牙低功耗通信、文件传输和云服务集成的复杂性，让您可以专注于构建出色的用户体验。

## 核心功能

- **设备管理**：扫描、连接和管理MemoTalk设备
- **录音控制**：通过单个命令启动、停止和暂停录音
- **文件管理**：列出、下载和删除设备上的录音
- **实时转录**：获取录音的实时转录
- **云端AI服务**：利用基于云的AI进行转录、摘要等功能
- **OTA固件更新**：保持设备固件始终处于最新版本
- **跨平台**：支持Android和iOS，提供一致的API

## 架构设计

SDK基于现代化的响应式架构构建，具有良好的可扩展性和可维护性。

- **Kotlin多平台**：核心逻辑使用Kotlin编写，在Android和iOS之间共享
- **协程**：使用Kotlin协程处理异步操作，代码简洁清晰
- **分层架构**：API层、核心逻辑层和通信层之间职责清晰分离

## 快速开始

### Android集成

1.  **添加依赖**到您的`build.gradle`文件：

    ```gradle
    implementation 'com.memotalk:sdk:1.0.0'
    ```

2.  **初始化SDK**在您的`Application`类中：

    ```kotlin
    import com.memotalk.sdk.MemoTalkSDK
    import com.memotalk.sdk.SDKConfig

    class MainApplication : Application() {
        override fun onCreate() {
            super.onCreate()

            val config = SDKConfig(
                appKey = "YOUR_APP_KEY",
                appSecret = "YOUR_APP_SECRET"
            )
            MemoTalkSDK.initialize(this, config)
        }
    }
    ```

### iOS集成

1.  **添加Swift包**到您的Xcode项目：

    ```
    https://github.com/alanwangxiyu-wq/MemoTalk-SDK01.git
    ```

2.  **初始化SDK**在您的`AppDelegate`中：

    ```swift
    import MemoTalkSDK

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let config = SDKConfig(
            appKey: "YOUR_APP_KEY",
            appSecret: "YOUR_APP_SECRET"
        )
        MemoTalkSDK.initialize(config: config)
        return true
    }
    ```

## API参考

### DeviceManager（设备管理器）

-   `startScan()`：开始扫描附近的设备
-   `stopScan()`：停止扫描设备
-   `connect(device: Device)`：连接到特定设备
-   `disconnect(device: Device)`：断开设备连接
-   `getDeviceStatus(device: Device)`：获取设备的当前状态

### RecordingManager（录音管理器）

-   `startRecording(device: Device)`：开始新的录音
-   `stopRecording(device: Device)`：停止当前录音
-   `pauseRecording(device: Device)`：暂停当前录音
-   `getRecordingList(device: Device)`：获取设备上所有录音的列表

### FileManager（文件管理器）

-   `downloadRecording(device: Device, recording: Recording)`：从设备下载录音
-   `deleteRecording(device: Device, recording: Recording)`：删除设备上的录音
-   `getStorageInfo(device: Device)`：获取设备存储信息

### OTAManager（固件升级管理器）

-   `checkFirmwareVersion(device: Device)`：检查设备固件版本
-   `startOTAUpdate(device: Device, firmwareFile: File)`：启动固件升级

### TranscriptionManager（转录管理器）

-   `enableRealtimeTranscription(device: Device)`：启用实时转录
-   `transcribeRecording(device: Device, recording: Recording)`：转录录音
-   `summarizeRecording(device: Device, recording: Recording)`：生成录音摘要

## 文档

-   [Android API参考](docs/API_REFERENCE_ANDROID_CN.md)
-   [iOS API参考](docs/API_REFERENCE_IOS_CN.md)
-   [集成指南](docs/INTEGRATION_GUIDE_CN.md)
-   [问题排查手册](docs/TROUBLESHOOTING_CN.md)

## 示例应用

SDK包含了完整的Android和iOS示例应用，演示了所有核心功能的使用方法。

-   Android Demo：`android/demo/MainActivity.kt`
-   iOS Demo：`ios/demo/ViewController.swift`

## 技术支持

如有任何问题或需要技术支持，请通过以下方式联系我们：

-   邮箱：1762030061@qq.com
-   GitHub Issues：[https://github.com/alanwangxiyu-wq/MemoTalk-SDK01/issues](https://github.com/alanwangxiyu-wq/MemoTalk-SDK01/issues)

## 许可证

本项目采用MIT许可证。详见[LICENSE](LICENSE)文件。
