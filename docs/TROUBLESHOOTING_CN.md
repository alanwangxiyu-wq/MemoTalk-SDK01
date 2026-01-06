# MemoTalk SDK - 问题排查手册

---

本文档提供了使用MemoTalk SDK时可能遇到的常见问题及其解决方案。

## 目录

- [SDK初始化问题](#sdk初始化问题)
- [设备扫描问题](#设备扫描问题)
- [设备连接问题](#设备连接问题)
- [录音控制问题](#录音控制问题)
- [文件传输问题](#文件传输问题)
- [固件升级问题](#固件升级问题)
- [云服务问题](#云服务问题)
- [性能问题](#性能问题)

---

## SDK初始化问题

### 错误码：0

**问题描述**：SDK初始化失败。

**可能原因**：
1. 应用密钥（AppKey）或密钥（AppSecret）无效
2. 网络连接问题
3. SDK版本不兼容

**解决方案**：
1. 检查AppKey和AppSecret是否正确
2. 确保设备已连接到互联网
3. 更新到最新版本的SDK
4. 查看日志获取详细错误信息

**示例代码**：
```kotlin
val config = SDKConfig(
    appKey = "YOUR_VALID_APP_KEY",
    appSecret = "YOUR_VALID_APP_SECRET",
    logLevel = LogLevel.DEBUG  // 启用调试日志
)

val result = MemoTalkSDK.initialize(context, config)
when (result) {
    is Result.Failure -> {
        Log.e("MemoTalk", "初始化失败: ${result.error.code} - ${result.error.message}")
    }
}
```

---

## 设备扫描问题

### 错误码：1001 - 蓝牙不支持

**问题描述**：设备不支持蓝牙或蓝牙已禁用。

**解决方案**：
1. 检查设备是否支持蓝牙低功耗（BLE）
2. 确保蓝牙已在系统设置中启用
3. 引导用户启用蓝牙

**示例代码**：
```kotlin
val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
val bluetoothAdapter = bluetoothManager.adapter

if (bluetoothAdapter == null) {
    // 设备不支持蓝牙
    Toast.makeText(context, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show()
} else if (!bluetoothAdapter.isEnabled) {
    // 蓝牙未启用，请求用户启用
    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
}
```

### 错误码：1002 - 扫描失败

**问题描述**：无法启动蓝牙扫描。

**可能原因**：
1. 缺少必要的权限
2. 位置服务未启用（Android）
3. 蓝牙适配器繁忙

**解决方案**：
1. 确保已授予所有必需的蓝牙和位置权限
2. 在Android上启用位置服务
3. 停止其他正在进行的蓝牙操作
4. 重启蓝牙适配器

**Android权限检查**：
```kotlin
if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) 
    != PackageManager.PERMISSION_GRANTED) {
    // 请求权限
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_PERMISSIONS
    )
}
```

### 扫描不到设备

**问题描述**：扫描过程正常，但未发现任何设备。

**可能原因**：
1. 设备未开机或电量耗尽
2. 设备超出蓝牙范围（通常为10米）
3. 设备已被其他应用连接
4. 设备处于非广播模式

**解决方案**：
1. 确保MemoTalk设备已开机且电量充足
2. 将手机靠近设备（建议在2米以内）
3. 断开设备与其他应用的连接
4. 重启MemoTalk设备
5. 延长扫描时间（默认5秒可能不够）

---

## 设备连接问题

### 错误码：2001 - 连接失败

**问题描述**：无法连接到设备。

**可能原因**：
1. 设备已被其他应用连接
2. 蓝牙信号弱或干扰
3. 设备固件问题
4. 连接超时

**解决方案**：
1. 确保设备未连接到其他手机或应用
2. 减少手机与设备之间的距离
3. 避免在有强电磁干扰的环境中连接
4. 重启设备和手机的蓝牙
5. 更新设备固件到最新版本

**重试机制示例**：
```kotlin
suspend fun connectWithRetry(device: Device, maxRetries: Int = 3): Result<Unit, PlaudError> {
    repeat(maxRetries) { attempt ->
        val result = deviceManager.connect(device)
        if (result is Result.Success) {
            return result
        }
        
        if (attempt < maxRetries - 1) {
            delay(2000)  // 等待2秒后重试
            Log.d("MemoTalk", "连接失败，正在重试... (${attempt + 1}/$maxRetries)")
        }
    }
    return Result.failure(PlaudError(2001, "连接失败，已重试$maxRetries次"))
}
```

### 错误码：2002 - 断开失败

**问题描述**：无法断开设备连接。

**解决方案**：
1. 等待当前操作完成后再断开
2. 强制关闭应用并重新启动
3. 重启手机蓝牙

---

## 录音控制问题

### 错误码：4001 - 启动录音失败

**问题描述**：无法启动录音。

**可能原因**：
1. 设备未连接
2. 设备存储空间不足
3. 设备正在进行其他操作
4. 设备电量过低

**解决方案**：
1. 确保设备已成功连接
2. 检查设备存储空间
3. 等待当前操作完成
4. 为设备充电

**检查存储空间示例**：
```kotlin
val storageResult = fileManager.getStorageInfo(device)
when (storageResult) {
    is Result.Success -> {
        val (free, total) = storageResult.value
        val freePercent = (free.toDouble() / total * 100).toInt()
        
        if (freePercent < 10) {
            Toast.makeText(context, "设备存储空间不足，请清理后再录音", Toast.LENGTH_LONG).show()
        } else {
            // 开始录音
            recordingManager.startRecording(device)
        }
    }
}
```

### 错误码：4002 - 停止录音失败

**问题描述**：无法停止录音。

**解决方案**：
1. 等待几秒后重试
2. 检查设备连接状态
3. 重启设备

---

## 文件传输问题

### 错误码：5001 - 下载失败

**问题描述**：无法下载录音文件。

**可能原因**：
1. 蓝牙连接不稳定
2. 文件过大导致超时
3. 本地存储空间不足
4. 传输过程中断

**解决方案**：
1. 确保设备和手机距离较近
2. 避免在传输过程中进行其他操作
3. 检查手机存储空间
4. 使用WiFi传输模式（如果支持）
5. 实现断点续传

**断点续传示例**：
```kotlin
suspend fun downloadWithResume(device: Device, recording: Recording): Result<File, PlaudError> {
    var lastProgress = 0
    
    return fileManager.downloadRecording(device, recording) { progress ->
        if (progress > lastProgress) {
            lastProgress = progress
            Log.d("MemoTalk", "下载进度: $progress%")
        }
    }
}
```

### 传输速度慢

**问题描述**：文件传输速度很慢。

**优化建议**：
1. 使用WiFi传输代替蓝牙传输
2. 减少设备间距离
3. 避免同时进行其他蓝牙操作
4. 关闭其他占用蓝牙的应用
5. 在网络环境良好时进行传输

---

## 固件升级问题

### 错误码：6002 - OTA升级失败

**问题描述**：固件升级失败。

**可能原因**：
1. 固件文件损坏
2. 设备电量不足
3. 传输过程中断
4. 固件版本不兼容

**解决方案**：
1. 重新下载固件文件
2. 确保设备电量至少50%
3. 在升级过程中保持设备和手机距离较近
4. 不要在升级过程中断开连接或关闭应用
5. 验证固件文件的完整性

**安全升级示例**：
```kotlin
suspend fun safeOTAUpdate(device: Device, firmwareFile: File): Result<Unit, PlaudError> {
    // 1. 检查电量
    val statusResult = deviceManager.getDeviceStatus(device)
    if (statusResult is Result.Success) {
        if (statusResult.value.batteryLevel < 50) {
            return Result.failure(PlaudError(6002, "设备电量不足50%，请充电后再升级"))
        }
    }
    
    // 2. 检查固件文件
    if (!firmwareFile.exists() || firmwareFile.length() == 0L) {
        return Result.failure(PlaudError(6002, "固件文件无效"))
    }
    
    // 3. 开始升级
    return otaManager.startOTAUpdate(device, firmwareFile) { progress ->
        Log.d("MemoTalk", "升级进度: $progress%")
    }
}
```

---

## 云服务问题

### 错误码：7003 - 转录失败

**问题描述**：无法转录录音。

**可能原因**：
1. 网络连接问题
2. API密钥无效
3. 录音文件格式不支持
4. 云服务暂时不可用

**解决方案**：
1. 检查网络连接
2. 验证API密钥是否正确
3. 确保录音文件格式为OPUS
4. 稍后重试
5. 联系技术支持

---

## 性能问题

### 内存占用过高

**问题描述**：应用内存占用持续增长。

**解决方案**：
1. 及时释放不再使用的资源
2. 避免在主线程进行耗时操作
3. 使用完SDK后调用`release()`方法
4. 定期清理缓存文件

**资源管理示例**：
```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onDestroy() {
        super.onDestroy()
        
        // 释放SDK资源
        MemoTalkSDK.release()
        
        // 清理缓存
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}
```

### 电池消耗过快

**问题描述**：应用导致设备电池快速消耗。

**解决方案**：
1. 不使用时停止蓝牙扫描
2. 及时断开不需要的设备连接
3. 降低日志级别（生产环境使用ERROR级别）
4. 避免频繁的设备状态查询

---

## 获取帮助

如果以上解决方案无法解决您的问题，请通过以下方式获取帮助：

1. **查看日志**：启用DEBUG日志级别，查看详细的错误信息
2. **GitHub Issues**：在[GitHub仓库](https://github.com/alanwangxiyu-wq/MemoTalk-SDK01/issues)提交问题
3. **邮件支持**：发送邮件至1762030061@qq.com，附上详细的问题描述和日志

提交问题时，请提供以下信息：
- SDK版本
- 设备型号和操作系统版本
- 问题的详细描述和复现步骤
- 相关的日志输出
- 错误码和错误信息
