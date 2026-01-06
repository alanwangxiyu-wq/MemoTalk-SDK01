# MemoTalk SDK Android版 - API参考文档

---

## `MemoTalkSDK`

与SDK交互的主要入口点。

### `initialize(context: Context, config: SDKConfig): Result<Unit, PlaudError>`

初始化SDK。必须在使用任何其他SDK方法之前调用一次，通常在您的`Application`类中。

-   **参数**：
    -   `context`：应用程序上下文
    -   `config`：包含您的应用密钥和密钥的`SDKConfig`对象
-   **返回值**：表示成功或失败的`Result`对象

### `getInstance(): MemoTalkSDKImpl?`

获取SDK实现的单例实例。

### `getDeviceManager(): DeviceManager?`

获取用于设备扫描和连接的`DeviceManager`实例。

### `getRecordingManager(): RecordingManager?`

获取用于控制录音的`RecordingManager`实例。

### `getFileManager(): FileManager?`

获取用于文件操作的`FileManager`实例。

### `getOTAManager(): OTAManager?`

获取用于固件更新的`OTAManager`实例。

### `getTranscriptionManager(): TranscriptionManager?`

获取用于AI服务的`TranscriptionManager`实例。

### `release()`

释放SDK使用的所有资源。应在应用程序关闭时调用。

---

## `DeviceManager`

管理设备发现和连接。

### `startScan(): Result<List<Device>, PlaudError>`

开始扫描附近的MemoTalk设备。

-   **返回值**：包含已发现的`Device`对象列表的`Result`

### `stopScan(): Result<Unit, PlaudError>`

停止设备扫描。

### `connect(device: Device): Result<Unit, PlaudError>`

连接到特定设备。

-   **参数**：
    -   `device`：要连接的`Device`对象

### `disconnect(device: Device): Result<Unit, PlaudError>`

断开设备连接。

### `getDeviceStatus(device: Device): Result<DeviceStatus, PlaudError>`

获取设备的实时状态（电量、存储等）。

---

## `RecordingManager`

控制设备的录音功能。

### `startRecording(device: Device): Result<Unit, PlaudError>`

在设备上开始新的录音。

### `stopRecording(device: Device): Result<Unit, PlaudError>`

停止当前录音。

### `pauseRecording(device: Device): Result<Unit, PlaudError>`

暂停当前录音。

### `resumeRecording(device: Device): Result<Unit, PlaudError>`

恢复当前录音。

### `getRecordingList(device: Device): Result<List<Recording>, PlaudError>`

检索存储在设备上的所有录音列表。

### `clearAllRecordings(device: Device): Result<Unit, PlaudError>`

清空设备上的所有录音文件。

---

## `FileManager`

处理文件相关操作。

### `downloadRecording(device: Device, recording: Recording, progressCallback: (Int) -> Unit): Result<File, PlaudError>`

从设备下载录音文件到应用程序的本地缓存。

-   **参数**：
    -   `recording`：要下载的`Recording`
    -   `progressCallback`：接收下载进度更新（0-100）的lambda函数
-   **返回值**：包含已下载的`File`对象的`Result`

### `deleteRecording(device: Device, recording: Recording): Result<Unit, PlaudError>`

从设备删除录音。

### `getStorageInfo(device: Device): Result<Pair<Long, Long>, PlaudError>`

获取设备的存储信息。

-   **返回值**：包含（可用空间，总空间）的`Pair`对象

### `uploadRecordingToCloud(recording: Recording, filePath: String): Result<String, PlaudError>`

将录音上传到云端服务。

-   **返回值**：包含上传ID的`Result`

---

## `OTAManager`

管理固件更新。

### `checkFirmwareVersion(device: Device): Result<String, PlaudError>`

检查设备的当前固件版本。

### `startOTAUpdate(device: Device, firmwareFile: File, progressCallback: (Int) -> Unit): Result<Unit, PlaudError>`

启动空中固件更新（OTA）过程。

-   **参数**：
    -   `firmwareFile`：固件文件
    -   `progressCallback`：接收更新进度（0-100）的lambda函数

### `syncDeviceTime(device: Device): Result<Unit, PlaudError>`

同步设备时间。

---

## `TranscriptionManager`

与基于云的AI服务集成。

### `enableRealtimeTranscription(device: Device): Result<Unit, PlaudError>`

启用实时转录功能。

### `disableRealtimeTranscription(device: Device): Result<Unit, PlaudError>`

禁用实时转录功能。

### `transcribeRecording(device: Device, recording: Recording): Result<String, PlaudError>`

将录音提交到云端进行转录。

-   **返回值**：包含转录文本的`Result`

### `summarizeRecording(device: Device, recording: Recording): Result<String, PlaudError>`

将录音提交到云端生成摘要。

-   **返回值**：包含摘要文本的`Result`

---

## 数据模型

### `Device`

表示一个蓝牙设备。

-   `name: String?`：设备名称
-   `address: String`：设备MAC地址
-   `rssi: Int`：信号强度
-   `connectionState: ConnectionState`：连接状态

### `Recording`

表示一个录音文件。

-   `fileId: String`：文件ID
-   `duration: Long`：录音时长（毫秒）
-   `timestamp: Long`：创建时间戳
-   `fileSize: Long`：文件大小（字节）
-   `isSynced: Boolean`：是否已同步到云端

### `DeviceStatus`

表示设备状态。

-   `batteryLevel: Int`：电量（0-100）
-   `storageFree: Long`：可用存储空间（字节）
-   `storageTotal: Long`：总存储空间（字节）
-   `isRecording: Boolean`：是否正在录音

### `PlaudError`

表示错误信息。

-   `code: Int`：错误码
-   `message: String`：错误描述

---

## 错误码参考

| 错误码范围 | 类别 | 说明 |
|:---|:---|:---|
| 0 | SDK初始化 | SDK初始化失败 |
| 1001-1999 | 设备扫描 | 蓝牙不支持、扫描失败等 |
| 2001-2999 | 设备连接 | 连接失败、断开失败等 |
| 3001-3999 | 设备状态 | 状态查询失败 |
| 4001-4999 | 录音控制 | 录音启动/停止失败等 |
| 5001-5999 | 文件操作 | 下载/删除/上传失败等 |
| 6001-6999 | 固件升级 | OTA升级失败等 |
| 7001-7999 | AI服务 | 转录/摘要失败等 |
