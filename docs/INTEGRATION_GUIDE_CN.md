# MemoTalk SDK - 集成指南

---

本指南提供将MemoTalk SDK集成到Android和iOS应用程序的分步说明。

## Android集成

### 1. 前置要求

-   Android Studio 2023.2.1或更高版本
-   Android API级别26（Android 8.0）或更高
-   Kotlin 1.8.0或更高版本

### 2. 添加依赖

将以下行添加到您应用级`build.gradle.kts`文件的`dependencies`块中：

```kotlin
implementation("com.memotalk:sdk:1.0.0")
```

### 3. 权限配置

在您的`AndroidManifest.xml`文件中声明以下权限：

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### 4. SDK初始化

在您的`Application`类中初始化SDK：

```kotlin
import com.memotalk.sdk.MemoTalkSDK
import com.memotalk.sdk.SDKConfig
import com.memotalk.sdk.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = SDKConfig(
            appKey = "YOUR_APP_KEY",
            appSecret = "YOUR_APP_SECRET",
            logLevel = LogLevel.DEBUG  // 生产环境建议使用LogLevel.ERROR
        )
        
        val result = MemoTalkSDK.initialize(this, config)
        when (result) {
            is Result.Success -> {
                Log.d("MemoTalk", "SDK初始化成功")
            }
            is Result.Failure -> {
                Log.e("MemoTalk", "SDK初始化失败: ${result.error.message}")
            }
        }
    }
}
```

### 5. 运行时权限请求

在Android 6.0+上，您需要在运行时请求蓝牙和位置权限：

```kotlin
private val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val allGranted = permissions.values.all { it }
    if (allGranted) {
        // 权限已授予，可以开始扫描设备
        startDeviceScan()
    } else {
        // 权限被拒绝
        Toast.makeText(this, "需要蓝牙和位置权限", Toast.LENGTH_SHORT).show()
    }
}

private fun requestPermissions() {
    val permissions = mutableListOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    
    permissionLauncher.launch(permissions.toTypedArray())
}
```

### 6. 基本使用示例

```kotlin
import com.memotalk.sdk.MemoTalkSDK
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var deviceManager: DeviceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        deviceManager = MemoTalkSDK.getDeviceManager()!!
        
        // 扫描设备
        lifecycleScope.launch {
            val result = deviceManager.startScan()
            when (result) {
                is Result.Success -> {
                    val devices = result.value
                    // 显示设备列表
                }
                is Result.Failure -> {
                    // 处理错误
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        MemoTalkSDK.release()
    }
}
```

---

## iOS集成

### 1. 前置要求

-   Xcode 14.3或更高版本
-   iOS 12.0或更高版本
-   Swift 5.8或更高版本

### 2. 添加Swift包

在Xcode中，转到`File > Add Packages...`并输入以下仓库URL：

```
https://github.com/alanwangxiyu-wq/MemoTalk-SDK01.git
```

选择版本`1.0.0`或更高版本，然后点击`Add Package`。

### 3. 权限配置

在您的`Info.plist`文件中添加以下键：

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>我们需要蓝牙权限来连接您的MemoTalk设备</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>我们需要蓝牙权限来连接您的MemoTalk设备</string>
```

### 4. SDK初始化

在您的`AppDelegate`中初始化SDK：

```swift
import UIKit
import MemoTalkSDK

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        let config = SDKConfig(
            appKey: "YOUR_APP_KEY",
            appSecret: "YOUR_APP_SECRET",
            logLevel: .debug  // 生产环境建议使用.error
        )
        
        let result = MemoTalkSDK.initialize(config: config)
        switch result {
        case .success:
            print("SDK初始化成功")
        case .failure(let error):
            print("SDK初始化失败: \(error.message)")
        }
        
        return true
    }
}
```

### 5. 基本使用示例

```swift
import UIKit
import MemoTalkSDK

class ViewController: UIViewController {
    
    private var deviceManager: DeviceManager?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        deviceManager = MemoTalkSDK.getDeviceManager()
        
        // 扫描设备
        Task {
            guard let deviceManager = deviceManager else { return }
            let result = await deviceManager.startScan()
            
            switch result {
            case .success(let devices):
                // 显示设备列表
                print("发现 \(devices.count) 个设备")
            case .failure(let error):
                // 处理错误
                print("扫描失败: \(error.message)")
            }
        }
    }
    
    deinit {
        MemoTalkSDK.release()
    }
}
```

---

## 常见问题

### 1. 蓝牙权限被拒绝

**问题**：应用无法扫描或连接设备。

**解决方案**：
- 确保在`AndroidManifest.xml`（Android）或`Info.plist`（iOS）中声明了所有必需的权限。
- 在运行时请求权限，并处理用户拒绝的情况。
- 引导用户到系统设置中手动授予权限。

### 2. 设备扫描不到

**问题**：调用`startScan()`后没有发现任何设备。

**解决方案**：
- 确保设备已开机且蓝牙已启用。
- 确保设备在蓝牙范围内（通常为10米）。
- 检查设备是否已被其他应用连接。
- 在Android上，确保位置服务已启用（蓝牙扫描需要位置权限）。

### 3. 连接失败

**问题**：调用`connect()`后连接失败。

**解决方案**：
- 确保设备未被其他应用连接。
- 尝试重启设备和手机的蓝牙。
- 检查设备电量是否充足。
- 查看错误码和错误信息以获取更多详细信息。

### 4. 文件传输速度慢

**问题**：下载录音文件速度很慢。

**解决方案**：
- 确保设备和手机之间的距离较近，减少信号干扰。
- 避免在蓝牙传输过程中进行其他高负载操作。
- 考虑使用WiFi传输模式（如果设备支持）。

---

## 下一步

- 查看[Android API参考](API_REFERENCE_ANDROID_CN.md)了解所有可用的API。
- 查看[iOS API参考](API_REFERENCE_IOS_CN.md)了解所有可用的API。
- 查看[问题排查手册](TROUBLESHOOTING_CN.md)解决常见问题。
- 运行示例应用以了解SDK的完整功能。
